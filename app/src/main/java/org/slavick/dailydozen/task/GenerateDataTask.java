package org.slavick.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import hugo.weaving.DebugLog;

public class GenerateDataTask extends TaskWithContext<Boolean, Integer, Boolean> {
    private Boolean generateRandomData;
    private Random random;

    public GenerateDataTask(Context context) {
        super(context);

        random = new Random();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setCancelable(false);
        progress.setTitle(R.string.task_generating_random_data);
        progress.show();
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        if (params != null && params.length > 0) {
            generateRandomData = params[0];
        }

        deleteAllExistingData();

        final List<Food> allFoods = Food.getAllFoods();

        final int numDays = 365;

        final DateTime today = DateTime.today(TimeZone.getDefault());
        DateTime current = today.minusDays(numDays);

        int i = 0;

        while (current.lteq(today)) {
            createServingsForDay(allFoods, current);

            publishProgress(++i, numDays);

            current = current.plusDays(1);
        }

        return null;
    }

    @DebugLog
    private void createServingsForDay(List<Food> allFoods, DateTime current) {
        ActiveAndroid.beginTransaction();

        try {
            final Day day = new Day(current);
            day.save();

            for (Food food : allFoods) {
                final int recommendedServings = food.getRecommendedServings();
                final int numServings = generateRandomData ? random.nextInt(recommendedServings + 1) : recommendedServings;

                if (numServings > 0) {
                    Servings.createServingsIfDoesNotExist(day, food, numServings);
                }
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void deleteAllExistingData() {
        Servings.truncate(Servings.class);
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
