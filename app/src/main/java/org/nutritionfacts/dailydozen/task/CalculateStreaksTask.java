package org.nutritionfacts.dailydozen.task;

import com.activeandroid.ActiveAndroid;

import org.jetbrains.annotations.NotNull;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import java.util.List;

public class CalculateStreaksTask extends BaseTask<Boolean> {
    private final ProgressListener progressListener;

    public CalculateStreaksTask(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Boolean call() {
        final List<Day> allDays = Day.getAllDays();

        boolean calcFoodStreaksResult = calculateFoodStreaks(allDays);
        boolean calcTweakStreaksResult = calculateTweakStreaks(allDays);
        return calcFoodStreaksResult && calcTweakStreaksResult;
    }

    @Override
    public void setUiForLoading() {
        progressListener.showProgressBar(R.string.task_calculating_streaks_title);
    }

    @Override
    public void setDataAfterLoading(Boolean result) {
        progressListener.hideProgressBar();

        Bus.calculateStreaksComplete(result);
    }

    @NotNull
    private Boolean calculateFoodStreaks(List<Day> allDays) {
        final List<Food> allFoods = Food.getAllFoods();
        final int numFoods = allFoods.size();

        for (int i = 0; i < numFoods; i++) {
            ActiveAndroid.beginTransaction();

            Food food = allFoods.get(i);

            try {
                for (Day day : allDays) {
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

            progressListener.updateProgressBar(i + 1, numFoods);
        }

        return true;
    }

    @NotNull
    private Boolean calculateTweakStreaks(List<Day> allDays) {
        final List<Tweak> allTweaks = Tweak.getAllTweaks();
        final int numTweaks = allTweaks.size();

        for (int i = 0; i < numTweaks; i++) {
            ActiveAndroid.beginTransaction();

            Tweak tweak = allTweaks.get(i);

            try {
                for (Day day : allDays) {
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

            progressListener.updateProgressBar(i + 1, numTweaks);
        }

        return true;
    }
}
