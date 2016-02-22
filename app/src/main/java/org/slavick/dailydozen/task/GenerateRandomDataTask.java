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

public class GenerateRandomDataTask extends TaskWithContext<Void, Integer, Boolean> {
    private Random random;

    public GenerateRandomDataTask(Context context) {
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
    protected Boolean doInBackground(Void... params) {
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
                final int numServings = random.nextInt(food.getRecommendedServings() + 1);
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
