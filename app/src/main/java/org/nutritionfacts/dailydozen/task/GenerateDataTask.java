package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.model.Weights;
import org.nutritionfacts.dailydozen.task.params.GenerateDataTaskParams;

import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import hugo.weaving.DebugLog;

public class GenerateDataTask extends TaskWithContext<GenerateDataTaskParams, Integer, Boolean> {
    private GenerateDataTaskParams taskParams;
    private Random random;

    public GenerateDataTask(Context context) {
        super(context);

        random = new Random();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(R.string.task_generating_random_data);
        progress.show();
    }

    @Override
    protected Boolean doInBackground(GenerateDataTaskParams... params) {
        if (params != null && params.length > 0) {
            taskParams = params[0];
        }

        deleteAllExistingData();

        final List<Food> allFoods = Food.getAllFoods();
        final List<Tweak> allTweaks = Tweak.getAllTweaks();

        final int numDays = taskParams.getHistoryToGenerate();

        final DateTime today = DateTime.today(TimeZone.getDefault());
        DateTime current = today.minusDays(numDays);

        int i = 0;

        while (current.lteq(today)) {
            if (taskParams.generateRandomData()) {
                // Give a 20% chance of not creating servings for the day
                if (random.nextInt(5) >= 1) {
                    createUserDataForDay(allFoods, allTweaks, current);
                }
            } else {
                // Always generate data if generating full data
                createUserDataForDay(allFoods, allTweaks, current);
            }

            publishProgress(++i, numDays);

            current = current.plusDays(1);
        }

        return null;
    }

    @DebugLog
    private void createUserDataForDay(List<Food> allFoods, List<Tweak> allTweaks, DateTime current) {
        ActiveAndroid.beginTransaction();

        try {
            final Day day = new Day(current);
            day.save();

            createDailyDozenForDay(allFoods, day);
            createTweaksForDay(allTweaks, day);
            createWeightsForDay(day);

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void createDailyDozenForDay(List<Food> allFoods, Day day) {
        for (Food food : allFoods) {
            final int recommendedServings = food.getRecommendedAmount();
            final int numServings = taskParams.generateRandomData() ? random.nextInt(recommendedServings + 1) : recommendedServings;

            if (numServings > 0) {
                DDServings.createServingsIfDoesNotExist(day, food, numServings);
            }
        }
    }

    private void createTweaksForDay(List<Tweak> allTweaks, Day day) {
        for (Tweak tweak : allTweaks) {
            final int recommendedServings = tweak.getRecommendedAmount();
            final int numServings = taskParams.generateRandomData() ? random.nextInt(recommendedServings + 1) : recommendedServings;

            if (numServings > 0) {
                TweakServings.createServingsIfDoesNotExist(day, tweak, numServings);
            }
        }
    }

    private void createWeightsForDay(final Day day) {
        float morningWeight = 0, eveningWeight = 0;

        // Give a 80% chance of setting morning weight
        if (random.nextInt(10) >= 2) {
            morningWeight = round(180 + 3 * random.nextFloat(), 1);
        }

        // Give a 50% chance of setting evening weight
        if (random.nextInt(10) >= 5) {
            eveningWeight = round(180 + 3 * random.nextFloat(), 1);
        }

        Weights.createWeightsIfDoesNotExist(day, morningWeight, eveningWeight);
    }

    private static float round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    private void deleteAllExistingData() {
        DDServings.truncate(DDServings.class);
        TweakServings.truncate(TweakServings.class);
        Weights.truncate(Weights.class);
        Day.truncate(Day.class);
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
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
