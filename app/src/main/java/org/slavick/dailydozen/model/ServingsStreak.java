package org.slavick.dailydozen.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;

@Table(name = "servings_streak")
public class ServingsStreak extends TruncatableModel {
    private final static String TAG = ServingsStreak.class.getSimpleName();

    @Column(name = "date_id")
    private Day date;

    @Column(name = "food_id")
    private Food food;

    @Column(name = "streak")
    private int streak;

    public ServingsStreak() {
    }

    public ServingsStreak(Day day, Food food, int streak) {
        this.date = day;
        this.food = food;
        this.streak = streak;
    }

    public Day getDate() {
        return date;
    }

    public Food getFood() {
        return food;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public static ServingsStreak getStreakOnDateForFood(Date date, Food food) {
        return date != null ? getStreakOnDateForFood(Day.getByDate(date), food) : null;

    }

    public static ServingsStreak getStreakOnDateForFood(Day day, Food food) {
        if (day != null && day.getId() != null && food != null && food.getId() != null) {
            final ServingsStreak streak = new Select().from(ServingsStreak.class)
                    .where("date_id = ?", day.getId())
                    .and("food_id = ?", food.getId())
                    .executeSingle();

            if (streak != null) {
                return streak;
            }
        }

        return null;
    }

    public static boolean isEmpty() {
        return new Select().from(ServingsStreak.class).count() == 0;
    }

    public static void setStreakOnDateForFood(Day day, Food food, int currentStreak) {
        ServingsStreak servingsStreak = getStreakOnDateForFood(day, food);

        if (servingsStreak == null) {
            servingsStreak = new ServingsStreak(day, food, currentStreak);
        }

        servingsStreak.setStreak(currentStreak);
        servingsStreak.save();
    }
}
