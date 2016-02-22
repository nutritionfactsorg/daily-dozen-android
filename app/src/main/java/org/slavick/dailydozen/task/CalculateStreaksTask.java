package org.slavick.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.List;

public class CalculateStreaksTask extends TaskWithContext<Void, Integer, Boolean> {
    private final static String TAG = CalculateStreaksTask.class.getSimpleName();

    private final Listener listener;

    public interface Listener {
        void onCalculateStreaksComplete(boolean success);
    }

    public CalculateStreaksTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (Servings.isEmpty()) {
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

                    final Servings servingsOnDate = Servings.getByDateAndFood(day, food);
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

        if (listener != null) {
            listener.onCalculateStreaksComplete(success);
        }
    }
}
