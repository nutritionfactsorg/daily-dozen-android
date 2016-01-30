package org.slavick.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.List;

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

        final Day startingDay = input.getStartingDay();
        final Food food = input.getFood();

        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
                if (isCancelled()) {
                    return false;
                }

                final Day day = daysToCalculate.get(i);

                final Servings servingsOnDate = Servings.getByDateAndFood(day, food);
                if (servingsOnDate != null) {
                    servingsOnDate.recalculateStreak();
                    servingsOnDate.save();
                }
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (listener != null) {
            listener.onCalculateStreakComplete(success);
        }
    }
}
