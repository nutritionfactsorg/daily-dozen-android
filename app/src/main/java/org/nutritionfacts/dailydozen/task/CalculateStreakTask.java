package org.nutritionfacts.dailydozen.task;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.Servings;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import java.util.List;

public class CalculateStreakTask extends BaseTask<Boolean> {
    private final Day startingDay;
    private Food food;
    private Tweak tweak;

    private boolean isFoodStreak;

    public CalculateStreakTask(StreakTaskInput input) {
        this.startingDay = input.getStartingDay();

        if (input.getFood() != null) {
            isFoodStreak = true;
            this.food = input.getFood();
        } else if (input.getTweak() != null) {
            isFoodStreak = false;
            this.tweak = input.getTweak();
        }
    }

    @Override
    public Boolean call() {
        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
                final Day day = daysToCalculate.get(i);

                Servings servingsOnDate;
                if (isFoodStreak) {
                    servingsOnDate = DDServings.getByDateAndFood(day, food);
                } else {
                    servingsOnDate = TweakServings.getByDateAndTweak(day, tweak);
                }

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
            if (isFoodStreak) {
                Bus.foodServingsChangedEvent(startingDay, food);
            } else {
                Bus.tweakServingsChangedEvent(startingDay, tweak);
            }
        }
    }
}
