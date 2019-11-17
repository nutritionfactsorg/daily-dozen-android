package org.nutritionfacts.dailydozen.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.nutritionfacts.dailydozen.Servings;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@Table(name = "tweak_servings")
public class TweakServings extends TruncatableModel implements Servings {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "tweak_id")
    private Tweak tweak;

    @Column(name = "servings")
    private int servings;

    @Column(name = "streak")
    private int streak;

    public TweakServings() {
    }

    public TweakServings(Day day, Tweak tweak) {
        this.day = day;
        this.tweak = tweak;
    }

    public Day getDay() {
        return day;
    }

    public Tweak getTweak() {
        return tweak;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void recalculateStreak() {
        if (servings == tweak.getRecommendedAmount()) {
            streak = getStreakFromDayBefore() + 1;
        } else if (servings < tweak.getRecommendedAmount()) {
            streak = 0;
        }
    }

    private int getStreakFromDayBefore() {
        try {
            final TweakServings servings = TweakServings.getByDateAndTweak(day.getDayBefore(), tweak);
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
        return String.format("[%s] [%s] [TweakServings %s]", tweak.toString(), day.toString(), getServings());
    }

    public static TweakServings getByDateAndTweak(Day day, Tweak tweak) {
        if (day != null && day.getId() != null && tweak != null && tweak.getId() != null) {
            return new Select().from(TweakServings.class)
                    .where("date_id = ?", day.getId())
                    .and("tweak_id = ?", tweak.getId())
                    .executeSingle();
        }

        return null;
    }

    public static TweakServings createServingsIfDoesNotExist(final Day day, final Tweak tweak) {
        return createServingsIfDoesNotExist(day, tweak, 0);
    }

    public static TweakServings createServingsIfDoesNotExist(final Day day, final Tweak tweak, final int numServings) {
        TweakServings servings = getByDateAndTweak(day, tweak);

        if (servings == null) {
            servings = new TweakServings(day, tweak);

            if (numServings > 0) {
                servings.setServings(numServings);
            }

            servings.save();
        }

        return servings;
    }

    public static List<TweakServings> getServingsOnDate(Day day) {
        List<TweakServings> servings = new ArrayList<>();

        if (day != null && day.getId() != null) {
            servings = new Select().from(TweakServings.class)
                    .where("date_id = ?", day.getId())
                    .execute();
        }

        return servings;
    }

    public static int getTotalTweakServingsOnDate(final Day day) {
        int numServings = 0;

        if (day != null && day.getId() != null) {
            numServings = SQLiteUtils.intQuery(
                    "SELECT SUM(servings) FROM tweak_servings WHERE date_id = ?",
                    new String[]{day.getId().toString()});
        }

        return numServings;
    }

    public static float getAverageTotalTweakServingsInYear(final int year) {
        return getAverageTweakServingsForDays(Day.getDaysInYear(year));
    }

    public static float getAverageTotalTweakServingsInMonth(final int year, final int monthOneBased) {
        return getAverageTweakServingsForDays(Day.getDaysInYearAndMonth(year, monthOneBased));
    }

    private static float getAverageTweakServingsForDays(final List<Day> days) {
        if (days == null || days.isEmpty()) {
            return 0;
        }

        int totalServings = 0;

        for (Day day : days) {
            totalServings += getTotalTweakServingsOnDate(day);
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

            final List<TweakServings> servings = SQLiteUtils.rawQuery(TweakServings.class,
                    String.format("SELECT * FROM servings WHERE food_id = ? AND date_id IN (%s)", placeholders),
                    argsArray);

            for (TweakServings serving : servings) {
                servingsInMonth.put(
                        serving.getDay(),
                        serving.getServings() == food.getRecommendedAmount());
            }
        }

        return servingsInMonth;
    }

    public static boolean isEmpty() {
        return new Select().from(TweakServings.class).count() == 0;
    }
}
