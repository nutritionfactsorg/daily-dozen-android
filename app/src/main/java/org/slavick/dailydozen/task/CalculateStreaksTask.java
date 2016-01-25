package org.slavick.dailydozen.task;

import android.content.Context;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.model.ServingsStreak;

import java.util.List;

public class CalculateStreaksTask extends TaskWithContext<Void, Integer, Boolean> {
    private final static String TAG = CalculateStreaksTask.class.getSimpleName();

    private final Listener listener;

    public interface Listener {
        void onComplete(boolean success);
    }

    public CalculateStreaksTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<Day> allDays = Day.getAllDays();
        List<Food> allFoods = Food.getAllFoods();

        if (!isEmpty(allDays)) {
            final int numDays = allDays.size();

            for (Food food : allFoods) {
                int currentStreak = 0;

                for (int i = 0; i < numDays; i++) {
                    final Day day = allDays.get(i);

                    if (isCancelled()) {
                        return false;
                    }

                    Servings foodServingsOnDate = Servings.getByDateAndFood(day.getDateObject(), food);
                    if (foodServingsOnDate != null) {
                        if (foodServingsOnDate.getServings() == food.getRecommendedServings()) {
                            currentStreak++;
                        } else {
                            currentStreak = 0;
                        }

                        if (currentStreak > 0) {
                            final ServingsStreak streak = new ServingsStreak(day, food, currentStreak);
                            streak.save();
                        }

                        publishProgress(i + 1, numDays);
                    }
                }
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
            listener.onComplete(success);
        }
    }
}
