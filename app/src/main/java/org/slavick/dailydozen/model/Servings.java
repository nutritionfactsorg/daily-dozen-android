package org.slavick.dailydozen.model;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Table(name = "servings")
public class Servings extends TruncatableModel {
    private final static String TAG = Servings.class.getSimpleName();

    @Column(name = "date_id")
    private Day date;

    @Column(name = "food_id")
    private Food food;

    @Column(name = "servings")
    private int servings;

    public Servings() {
    }

    public Servings(Day date, Food food) {
        this.date = date;
        this.food = food;
    }

    public Day getDate() {
        return date;
    }

    public Food getFood() {
        return food;
    }

    public int getServings() {
        return servings;
    }

    public void increaseServings() {
        if (servings < food.getRecommendedServings()) {
            servings++;
        }
    }

    public void decreaseServings() {
        if (servings > 0) {
            servings--;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] [Servings %s]", food.toString(), date.toString(), servings);
    }

    public static Servings getByDateAndFood(final Date date, final Food food) {
        if (date != null) {
            final Day day = Day.getByDate(date);

            if (day != null && day.getId() != null && food != null && food.getId() != null) {
                return new Select().from(Servings.class)
                        .where("date_id = ?", day.getId())
                        .and("food_id = ?", food.getId())
                        .executeSingle();
            }
        }

        return null;
    }

    public static Servings createServingsIfDoesNotExist(final Date date, final Food food) {
        return createServingsIfDoesNotExist(date, food, 0);
    }

    public static Servings createServingsIfDoesNotExist(final Date date, final Food food, final int numServings) {
        Servings servings = getByDateAndFood(date, food);

        if (servings == null) {
            servings = new Servings(Day.createDateIfDoesNotExist(date), food);
            servings.servings = numServings;
            servings.save();
        }

        return servings;
    }

    private static List<Servings> getAllServingsOnDate(Day day) {
        List<Servings> servings = new ArrayList<>();

        if (day != null) {
            servings = new Select().from(Servings.class)
                    .where("date_id = ?", day.getId())
                    .execute();
        }

        return servings;
    }

    public static int getTotalServingsOnDate(final Day day) {
        int numServings = 0;

        for (Servings serving : getAllServingsOnDate(day)) {
            numServings += serving.getServings();
        }

        return numServings;
    }

    public static int getTotalServingsOnDate(final Date date) {
        return getTotalServingsOnDate(Day.getByDate(date));
    }

    // Any Dates in the return map indicate that at least one serving of the food was consumed on that date.
    // The Boolean for the date indicates whether the number of servings equals the recommended servings of the food.
    public static Map<Date, Boolean> getServingsOfFoodInMonth(final long foodId, final Calendar calendar) {
        final Map<Date, Boolean> servingsInMonth = new ArrayMap<>();

        final Food food = Food.getById(foodId);

        if (food != null) {
            final List<Day> datesInMonth = new Select().from(Day.class)
                    .where("year = ?", calendar.get(Calendar.YEAR))
                    .and("month = ?", calendar.get(Calendar.MONTH) + 1)
                    .execute();

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
                        serving.getDate().getDateObject(),
                        serving.getServings() == food.getRecommendedServings());
            }
        }

        return servingsInMonth;
    }

    public static int getTotalServingsCount() {
        return new Select().from(Servings.class).count();
    }
}
