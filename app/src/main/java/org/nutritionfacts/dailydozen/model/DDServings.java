package org.nutritionfacts.dailydozen.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.Servings;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@Table(name = "servings")
public class DDServings extends TruncatableModel implements Servings {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "food_id")
    private Food food;

    @Column(name = "servings")
    private int servings;

    @Column(name = "streak")
    private int streak;

    public DDServings() {
    }

    public DDServings(Day day, Food food) {
        this.day = day;
        this.food = food;
    }

    public Day getDay() {
        return day;
    }

    public Food getFood() {
        return food;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void recalculateStreak() {
        if (servings == food.getRecommendedAmount()) {
            streak = getStreakFromDayBefore() + 1;
        } else if (servings < food.getRecommendedAmount()) {
            streak = 0;
        }
    }

    private int getStreakFromDayBefore() {
        try {
            final DDServings servings = DDServings.getByDateAndFood(day.getDayBefore(), food);
            return servings != null ? servings.getStreak() : 0;
        } catch (InvalidDateException e) {
            Timber.e(e, "getStreakFromDayBefore: ");
        }

        return 0;
    }

    public int getStreak() {
        return streak;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[%s] [%s] [DDServings %s]", food.toString(), day.toString(), getServings());
    }

    public static DDServings getByDateAndFood(final Day day, final Food food) {
        if (day != null && day.getId() != null && food != null && food.getId() != null) {
            return new Select().from(DDServings.class)
                    .where("date_id = ?", day.getId())
                    .and("food_id = ?", food.getId())
                    .executeSingle();
        }

        return null;
    }

    public static DDServings createServingsIfDoesNotExist(final Day day, final Food food) {
        return createServingsIfDoesNotExist(day, food, 0);
    }

    public static DDServings createServingsIfDoesNotExist(final Day day, final Food food, final int numServings) {
        DDServings servings = getByDateAndFood(day, food);

        if (servings == null) {
            servings = new DDServings(day, food);

            if (numServings > 0) {
                servings.setServings(numServings);
            }

            servings.save();
        }

        return servings;
    }

    public static DDServings createServingsAndRecalculateStreak(final Day day, final Food food, final int numServings) {
        DDServings servings = getByDateAndFood(day, food);

        if (servings == null) {
            servings = new DDServings(day, food);

            if (numServings > 0) {
                servings.setServings(numServings);
                servings.recalculateStreak();
            }

            servings.save();
        }

        return servings;
    }

    public static List<DDServings> getServingsOnDate(Day day) {
        List<DDServings> servings = new ArrayList<>();

        if (day != null && day.getId() != null) {
            servings = new Select().from(DDServings.class)
                    .where("date_id = ?", day.getId())
                    .execute();
        }

        return servings;
    }

    public static int getTotalServingsOnDate(final Day day) {
        int numServings = 0;

        if (day != null && day.getId() != null) {
            // Calculate the total servings on a particular date, but ignore the values for Vitamin B12
            numServings = SQLiteUtils.intQuery(
                    "SELECT SUM(servings) FROM servings WHERE date_id = ? AND food_id not in (SELECT Id FROM foods WHERE id_name = ?)",
                    new String[]{day.getId().toString(), Common.VITAMIN_B12});
        }

        return numServings;
    }

    public static float getAverageTotalServingsInYear(final int year) {
        return getAverageServingsForDays(Day.getDaysInYear(year));
    }

    public static float getAverageTotalServingsInMonth(final int year, final int monthOneBased) {
        return getAverageServingsForDays(Day.getDaysInYearAndMonth(year, monthOneBased));
    }

    private static float getAverageServingsForDays(final List<Day> days) {
        if (days == null || days.isEmpty()) {
            return 0;
        }

        int totalServings = 0;

        for (Day day : days) {
            totalServings += getTotalServingsOnDate(day);
        }

        return (float) totalServings / days.size();
    }

    // Any Dates in the return map indicate that at least one serving of the food was consumed on that date.
    // The Boolean for the date indicates whether the number of servings equals the recommended servings of the food.
    public static Map<Day, Boolean> getServingsOfFoodInYearAndMonth(final long foodId, final int year, final int monthOneBased) {
        final Map<Day, Boolean> servingsInMonth = new ArrayMap<>();

        final Food food = Food.getById(foodId);

        if (food != null) {
            final List<Day> datesInMonth = Day.getDaysInYearAndMonth(year, monthOneBased);

            final List<String> placeholderArray = new ArrayList<>(datesInMonth.size());
            final List<String> dateIds = new ArrayList<>(datesInMonth.size());
            for (int i = 0; i < datesInMonth.size(); i++) {
                Long dateId = datesInMonth.get(i).getId();
                if (dateId != null) {
                    placeholderArray.add("?");
                    dateIds.add(String.valueOf(dateId));
                }
            }

            if (placeholderArray.size() == 0) {
                return new ArrayMap<>();
            }

            final String placeholders = TextUtils.join(",", placeholderArray);

            ArrayList<String> args = new ArrayList<>(dateIds);
            args.add(0, String.valueOf(foodId));
            String[] argsArray = new String[args.size()];
            argsArray = args.toArray(argsArray);

            final List<DDServings> servings = SQLiteUtils.rawQuery(DDServings.class,
                    String.format("SELECT * FROM servings WHERE food_id = ? AND date_id IN (%s)", placeholders),
                    argsArray);

            for (DDServings serving : servings) {
                if (serving.getServings() > 0) {
                    servingsInMonth.put(
                            serving.getDay(),
                            serving.getServings() == food.getRecommendedAmount());
                }
            }
        }

        return servingsInMonth;
    }

    public static boolean isEmpty() {
        return new Select().from(DDServings.class).count() == 0;
    }
}
