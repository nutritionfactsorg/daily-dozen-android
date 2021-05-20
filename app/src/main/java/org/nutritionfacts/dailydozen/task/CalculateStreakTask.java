package org.nutritionfacts.dailydozen.task;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

import java.util.List;

public class CalculateStreakTask extends BaseTask<Boolean> {
    private final Day startingDay;
    private final Food food;

    public CalculateStreakTask(StreakTaskInput input) {
        this.startingDay = input.getStartingDay();
        this.food = input.getFood();
    }

    @Override
    public Boolean call() {
        final List<Day> daysToCalculate = startingDay.getDaysAfter();
        final int numDays = daysToCalculate.size();

        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < numDays; i++) {
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
    public void setDataAfterLoading(Boolean success) {
        if (success) {
            Bus.foodServingsChangedEvent(startingDay, food);
        }
    }
}
