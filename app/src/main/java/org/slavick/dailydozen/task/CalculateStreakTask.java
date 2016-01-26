package org.slavick.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.model.ServingsStreak;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class CalculateStreakTask extends TaskWithContext<StreakTaskInput, Integer, Boolean> {
    private final Listener listener;

    public interface Listener {
        void onCalculateStreakComplete(boolean success);
    }

    public CalculateStreakTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.hide();
    }

    @Override
    protected Boolean doInBackground(StreakTaskInput... params) {
        final StreakTaskInput input = params[0];
        if (input == null) {
            return false;
        }

        final Date startingDate = input.getStartingDate();
        final Food food = input.getFood();

        final List<Day> daysToCalculate = Day.getDaysAfter(startingDate);
        final int numDays = daysToCalculate.size();

        int currentStreak = getCurrentStreak(startingDate, food);

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
                if (isCancelled()) {
                    return false;
                }

                final Day day = daysToCalculate.get(i);

                final Servings servingsOnDate = Servings.getByDateAndFood(day, food);

                if (servingsOnDate != null && servingsOnDate.getServings() == food.getRecommendedServings()) {
                    currentStreak++;
                } else {
                    currentStreak = 0;
                }

                ServingsStreak.setStreakOnDateForFood(day, food, currentStreak);
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    private int getCurrentStreak(Date startingDate, Food food) {
        final TimeZone timeZone = TimeZone.getDefault();
        final Date dateBefore = new Date(DateTime.forInstant(startingDate.getTime(), timeZone).minusDays(1).getMilliseconds(timeZone));

        final ServingsStreak streak = ServingsStreak.getStreakOnDateForFood(dateBefore, food);
        return streak != null ? streak.getStreak() : 0;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (listener != null) {
            listener.onCalculateStreakComplete(success);
        }
    }
}
