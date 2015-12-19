package org.slavick.dailydozen.model;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Table(name = "servings")
public class Servings extends Model {
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

    public static boolean exists(final Day date, final Food food) {
        return getByDateAndFood(date, food) != null;
    }

    public static Servings getByDateAndFood(final Day date, final Food food) {
        return new Select().from(Servings.class)
                .where("date_id = ?", date.getId())
                .and("food_id = ?", food.getId())
                .executeSingle();
    }

    public static Servings createServingsIfDoesNotExist(final Day date, final Food food) {
        Servings servings;

        if (!exists(date, food)) {
            servings = new Servings(date, food);
            servings.save();
        } else {
            servings = getByDateAndFood(date, food);
        }

        return servings;
    }

    public static List<Servings> getServingsOfFoodInMonth(final long foodId, final Calendar calendar) {
        final Day day = Day.getByDate(new Date(calendar.getTimeInMillis()));

        List<Servings> servingsInMonth = new ArrayList<>();

        if (day != null) {
            List<Day> datesInMonth = new Select().from(Day.class)
                    .where("year = ?", day.getYear())
                    .and("month = ?", day.getMonth())
                    .execute();

            String[] placeholderArray = new String[datesInMonth.size()];
            Long[] dateIds = new Long[datesInMonth.size()];
            for (int i = 0; i < datesInMonth.size(); i++) {
                placeholderArray[i] = "?";
                dateIds[i] = datesInMonth.get(i).getId();
            }

            final String placeholders = TextUtils.join(",", placeholderArray);

            servingsInMonth = new Select().from(Servings.class)
                    .where("food_id = ?", foodId)
                    .and(String.format("date_id IN (%s)", placeholders), dateIds)
                    .execute();
        }

        return servingsInMonth;
    }
}
