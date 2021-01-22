package org.nutritionfacts.dailydozen.task;

import android.content.Context;

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

public class CalculateStreaksTask extends TaskWithContext<Void, Integer, Boolean> {
    public CalculateStreaksTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (DDServings.isEmpty() || TweakServings.isEmpty()) {
            progress.hide();
            cancel(true);
        } else {
            progress.setTitle(R.string.task_calculating_streaks_title);
            progress.show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (isCancelled()) {
            return false;
        }

        final List<Day> allDays = Day.getAllDays();

        boolean calcFoodStreaksResult = calculateFoodStreaks(allDays);
        boolean calcTweakStreaksResult = calculateTweakStreaks(allDays);
        return calcFoodStreaksResult && calcTweakStreaksResult;
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
                    if (isCancelled()) {
                        return false;
                    }

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

            publishProgress(i + 1, numFoods);
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
                    if (isCancelled()) {
                        return false;
                    }

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

            publishProgress(i + 1, numTweaks);
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (values.length == 2) {
            progress.setProgress(values[0]);
            progress.setMax(values[1]);
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        Bus.calculateStreaksComplete(success);
    }
}
