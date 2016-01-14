package org.slavick.dailydozen.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupTask extends TaskWithContext<File, Integer, Boolean> {
    private final static String TAG = BackupTask.class.getSimpleName();

    private final Listener listener;

    private List<Day> allDays;
    private List<Food> allFoods;

    public interface Listener {
        void onBackupComplete(boolean success);
    }

    public BackupTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        allDays = Day.getAllDays();
        allFoods = Food.getAllFoods();

        if (isEmpty(allDays) || isEmpty(allFoods)) {
            onCancelled();
        }
    }

    @Override
    protected Boolean doInBackground(File... params) {
        final File backupFile = params[0];
        Log.d(TAG, "backupFilename = " + backupFile.getName());

        final int numDays = allDays.size();

        final List<String> csvLines = new ArrayList<>(numDays);

        csvLines.add(getHeadersLine());

        for (int i = 0; i < numDays; i++) {
            if (isCancelled()) {
                break;
            }

            csvLines.add(getDayLine(allDays.get(i)));

            publishProgress(i + 1, numDays);
        }

        final String backupText = TextUtils.join(getLineSeparator(), csvLines);
        Log.d(TAG, backupText);

        try {
            final FileWriter fileWriter = new FileWriter(backupFile);
            fileWriter.write(backupText);
            fileWriter.close();

            Log.d(TAG, backupFile.getAbsolutePath() + " successfully written");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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
        Common.showToast(context, context.getString(success ? R.string.backup_success : R.string.backup_failed));

        if (listener != null) {
            listener.onBackupComplete(success);
        }
    }

    private String getHeadersLine() {
        final List<String> headers = new ArrayList<>();

        headers.add("Date");

        for (Food food : allFoods) {
            headers.add(food.getName());
        }

        return convertListToCsv(headers);
    }

    private String getDayLine(Day day) {
        final List<String> line = new ArrayList<>();

        line.add(String.valueOf(day.getDate()));

        final Date date = day.getDateObject();

        // TODO: 1/5/16 this is horribly inefficient, but good enough for now
        for (Food food : allFoods) {
            final Servings serving = Servings.getByDateAndFood(date, food);
            line.add(serving != null ? String.valueOf(serving.getServings()) : "0");
        }

        return convertListToCsv(line);
    }

    private String convertListToCsv(final List<String> list) {
        return TextUtils.join(",", list);
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
