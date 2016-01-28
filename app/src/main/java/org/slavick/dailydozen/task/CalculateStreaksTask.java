package org.slavick.dailydozen.task;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.List;

public class CalculateStreaksTask extends TaskWithContext<Void, Integer, Boolean> {
    private final static String TAG = CalculateStreaksTask.class.getSimpleName();

    private List<Day> allDays;
    private List<Food> allFoods;

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

        allDays = Day.getAllDays();
        allFoods = Food.getAllFoods();

        if (isEmpty(allDays) || isEmpty(allFoods)) {
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

        final int numDays = allDays.size();

        for (Food food : allFoods) {
            ActiveAndroid.beginTransaction();

            try {
                for (int i = 0; i < numDays; i++) {
                    if (isCancelled()) {
                        return false;
                    }

                    final Servings servingsOnDate = Servings.getByDateAndFood(allDays.get(i), food);
                    if (servingsOnDate != null) {
                        servingsOnDate.recalculateStreak();
                        servingsOnDate.save();
                    }

                    publishProgress(i + 1, numDays);
                }

                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
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

        // TODO: 1/25/16 make these sound better
        final Context context = getContext();
        Common.showToast(context, success ? R.string.calculate_streaks_success : R.string.calculate_streaks_failed);

        if (listener != null) {
            listener.onCalculateStreaksComplete(success);
        }
    }
}
