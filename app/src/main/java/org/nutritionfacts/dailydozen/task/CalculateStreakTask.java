package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

import java.util.List;

public class CalculateStreakTask extends TaskWithContext<StreakTaskInput, Integer, Boolean> {
    private Day startingDay;
    private Food food;

    public CalculateStreakTask(Context context) {
        super(context);
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

        startingDay = input.getStartingDay();
        food = input.getFood();

        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
                if (isCancelled()) {
                    return false;
                }

                final Day day = daysToCalculate.get(i);

                final DDServings servingsOnDate = DDServings.getByDateAndFood(day, food);
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

        if (success) {
            Bus.foodServingsChangedEvent(startingDay, food);
        }
    }
}
