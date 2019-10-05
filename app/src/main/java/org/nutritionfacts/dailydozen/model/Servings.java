package org.nutritionfacts.dailydozen.model;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import android.text.TextUtils;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@Table(name = "servings")
public class Servings extends TruncatableModel {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "food_id")
    private Food food;

    @Column(name = "servings")
    private int servings;

    @Column(name = "streak")
    private int streak;

    public Servings() {
    }

    public Servings(Day day, Food food) {
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

        recalculateStreak();
    }

    public void recalculateStreak() {
        if (servings == food.getRecommendedServings()) {
            streak = getStreakFromDayBefore() + 1;
        } else if (servings < food.getRecommendedServings()) {
            streak = 0;
        }
    }

    private int getStreakFromDayBefore() {
        try {
            final Servings servings = Servings.getByDateAndFood(day.getDayBefore(), food);
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
        return String.format("[%s] [%s] [Servings %s]", food.toString(), day.toString(), getServings());
    }

    public static Servings getByDateAndFood(final Day day, final Food food) {
        if (day != null && day.getId() != null && food != null && food.getId() != null) {
            return new Select().from(Servings.class)
                    .where("date_id = ?", day.getId())
                    .and("food_id = ?", food.getId())
                    .executeSingle();
        }

        return null;
    }

    public static Servings createServingsIfDoesNotExist(final Day day, final Food food) {
        return createServingsIfDoesNotExist(day, food, 0);
    }

    public static Servings createServingsIfDoesNotExist(final Day day, final Food food, final int numServings) {
        Servings servings = getByDateAndFood(day, food);

        if (servings == null) {
            servings = new Servings(day, food);

            if (numServings > 0) {
                servings.setServings(numServings);
            }

            servings.save();
        }

        return servings;
    }

    public static List<Servings> getServingsOnDate(Day day) {
        List<Servings> servings = new ArrayList<>();

        if (day != null && day.getId() != null) {
            servings = new Select().from(Servings.class)
                    .where("date_id = ?", day.getId())
                    .execute();
        }

        return servings;
    }

    public static int getTotalServingsOnDate(final Day day) {
        int numServings = 0;

        if (day != null && day.getId() != null) {
            // Calculate the total servings on a particular date, but ignore the values for Vitamins B12 and D
            numServings = SQLiteUtils.intQuery(
                    "SELECT SUM(servings) FROM servings WHERE date_id = ? AND food_id not in (SELECT Id FROM foods WHERE id_name = ? OR id_name = ?)",
                    new String[]{day.getId().toString(), Common.VITAMIN_B12, Common.OMEGA3});
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

            final String[] placeholderArray = new String[datesInMonth.size()];
            final Long[] dateIds = new Long[datesInMonth.size()];
            for (int i = 0; i < datesInMonth.size(); i++) {
                placeholderArray[i] = "?";
                dateIds[i] = datesInMonth.get(i).getId();
            }

            final String placeholders = TextUtils.join(",", placeholderArray);

            final List<Servings> servings = new Select().from(Servings.class)
                    .where("food_id = ?", foodId)
                    .and(String.format("date_id IN (%s)", placeholders), dateIds)
                    .execute();

            for (Servings serving : servings) {
                servingsInMonth.put(
                        serving.getDay(),
                        serving.getServings() == food.getRecommendedServings());
            }
        }

        return servingsInMonth;
    }

    public static boolean isEmpty() {
        return new Select().from(Servings.class).count() == 0;
    }
}
