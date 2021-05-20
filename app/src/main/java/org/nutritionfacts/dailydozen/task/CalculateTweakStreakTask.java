package org.nutritionfacts.dailydozen.task;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import java.util.List;

public class CalculateTweakStreakTask extends BaseTask<Boolean> {
    private final Day startingDay;
    private final Tweak tweak;

    public CalculateTweakStreakTask(StreakTaskInput input) {
        this.startingDay = input.getStartingDay();
        this.tweak = input.getTweak();
    }

    @Override
    public Boolean call() {
        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
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
    public void setDataAfterLoading(Boolean success) {
        if (success) {
            Bus.tweakServingsChangedEvent(startingDay, tweak);
        }
    }
}
