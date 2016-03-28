package org.slavick.dailydozen.model;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.slavick.dailydozen.exception.InvalidDateException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Table(name = "servings")
public class Servings extends TruncatableModel {
    private final static String TAG = Servings.class.getSimpleName();

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

    private void setServings(int servings) {
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
            Log.e(TAG, "getStreakFromDayBefore: ", e);
        }

        return 0;
    }

    public int getStreak() {
        return streak;
    }

    public void increaseServings() {
        final int servings = getServings();

        if (servings < food.getRecommendedServings()) {
            setServings(servings + 1);
        }
    }

    public void decreaseServings() {
        final int servings = getServings();

        if (servings > 0) {
            setServings(servings - 1);
        }
    }

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
            for (Servings serving : getServingsOnDate(day)) {
                numServings += serving.getServings();
            }
        }

        return numServings;
    }

    public static int getAverageTotalServingsInYear(final Calendar calendar) {
        int totalServingsInYear = 0;

        final List<Day> daysInYear = Day.getDaysInYear(calendar);
        final int numDaysInYear = daysInYear.size();

        for (Day day : daysInYear) {
            totalServingsInYear += getTotalServingsOnDate(day);
        }

        return totalServingsInYear / numDaysInYear;
    }

    public static float getAverageTotalServingsInMonth(final Calendar calendar) {
        float totalServingsInMonth = 0;

        final List<Day> daysInMonth = Day.getDaysInMonth(calendar);
        final int numDaysInMonth = daysInMonth.size();

        for (Day day : daysInMonth) {
            totalServingsInMonth += getTotalServingsOnDate(day);
        }

        return totalServingsInMonth / numDaysInMonth;
    }

    // Any Dates in the return map indicate that at least one serving of the food was consumed on that date.
    // The Boolean for the date indicates whether the number of servings equals the recommended servings of the food.
    public static Map<Day, Boolean> getServingsOfFoodInMonth(final long foodId, final Calendar calendar) {
        final Map<Day, Boolean> servingsInMonth = new ArrayMap<>();

        final Food food = Food.getById(foodId);

        if (food != null) {
            final List<Day> datesInMonth = Day.getDaysInMonth(calendar);

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
