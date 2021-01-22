package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import java.util.List;

public class CalculateTweakStreakTask extends TaskWithContext<StreakTaskInput, Integer, Boolean> {
    private Day startingDay;
    private Tweak tweak;

    public CalculateTweakStreakTask(Context context) {
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
        tweak = input.getTweak();

        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
                if (isCancelled()) {
                    return false;
                }

                final Day day = daysToCalculate.get(i);

                final TweakServings servingsOnDate = TweakServings.getByDateAndTweak(day, tweak);
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
            Bus.tweakServingsChangedEvent(startingDay, tweak);
        }
    }
}
