package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

import java.util.List;

public class CalculateStreaksTask extends TaskWithContext<Void, Integer, Boolean> {
    public CalculateStreaksTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (DDServings.isEmpty()) {
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
