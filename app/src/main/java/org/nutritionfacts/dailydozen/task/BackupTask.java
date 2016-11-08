package org.nutritionfacts.dailydozen.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Servings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;

public class BackupTask extends TaskWithContext<File, Integer, Boolean> {
    private final static String TAG = BackupTask.class.getSimpleName();

    private List<Day> allDays;
    private List<Food> allFoods;

    public BackupTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (Servings.isEmpty()) {
            Common.showToast(getContext(), R.string.no_servings_recorded);

            progress.hide();
            cancel(true);
        } else {
            progress.setTitle(R.string.task_backup_title);
            progress.show();
        }
    }

    @Override
    protected Boolean doInBackground(File... params) {
        if (isCancelled()) {
            return false;
        }

        allDays = Day.getAllDays();
        allFoods = Food.getAllFoods();

        final File backupFile = params[0];
        Log.d(TAG, "backupFilename = " + backupFile.getName());

        final int numDays = allDays.size();

        final String lineSeparator = Common.getLineSeparator();

        final StringBuilder csvLines = new StringBuilder(getHeadersLine());

        for (int i = 0; i < numDays; i++) {
            if (!isCancelled()) {
                csvLines.append(String.format("%s%s", lineSeparator, getDayLine(allDays.get(i))));

                publishProgress(i + 1, numDays);
            }
        }

        if (!isCancelled()) {
            try {
                final FileWriter fileWriter = new FileWriter(backupFile);
                fileWriter.write(csvLines.toString());
                fileWriter.close();

                Log.d(TAG, "backup file successfully written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return !isCancelled();
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

        final Context context = getContext();
        Common.showToast(context, success ? R.string.backup_success : R.string.backup_failed);

        Bus.backupCompleteEvent(success);
    }

    @DebugLog
    private String getHeadersLine() {
        final StringBuilder headers = new StringBuilder("Date");

        for (Food food : allFoods) {
            headers.append(String.format(",%s", food.getName()));
        }

        return headers.toString();
    }

    @DebugLog
    private String getDayLine(Day day) {
        Map<Food, Integer> foodServingsMap = createFoodServingsLookup(day);

        final StringBuilder line = new StringBuilder(String.valueOf(day.getDate()));

        for (Food food : allFoods) {
            line.append(String.format(",%s",
                    foodServingsMap.containsKey(food) ? String.valueOf(foodServingsMap.get(food)) : "0"));
        }

        return line.toString();
    }

    // This method converts the List of Servings into a Map for much faster lookup.
    @NonNull
    private Map<Food, Integer> createFoodServingsLookup(Day day) {
        Map<Food, Integer> foodServingsMap = new ArrayMap<>();

        for (Servings servings : Servings.getServingsOnDate(day)) {
            foodServingsMap.put(servings.getFood(), servings.getServings());
        }

        return foodServingsMap;
    }
}
