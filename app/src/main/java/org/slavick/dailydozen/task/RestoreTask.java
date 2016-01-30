package org.slavick.dailydozen.task;

import android.content.Context;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hirondelle.date4j.DateTime;
import hugo.weaving.DebugLog;

public class RestoreTask extends TaskWithContext<Uri, Integer, Boolean> {
    private final static String TAG = RestoreTask.class.getSimpleName();

    private final Listener listener;

    private String[] headers;
    private ArrayMap<String, Food> foodLookup;

    public interface Listener {
        void onRestoreComplete(boolean success);
    }

    public RestoreTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;

        foodLookup = new ArrayMap<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(R.string.task_restore_title);
        progress.show();
    }

    @Override
    protected Boolean doInBackground(Uri... params) {
        final List<String> lines = readBackupFileLines(params[0]);

        if (!isEmpty(lines) && !isCancelled()) {
            deleteAllExistingData();

            headers = lines.get(0).split(",");

            final int numLines = lines.size();

            // Start at 1 to skip the headers line
            for (int i = 1; i < numLines; i++) {
                if (isCancelled()) {
                    return false;
                }

                restoreLine(lines.get(i));

                publishProgress(i + 1, numLines);
            }

            return true;
        }

        return false;
    }

    @DebugLog
    private void deleteAllExistingData() {
        Servings.truncate(Servings.class);
        Day.truncate(Day.class);
    }

    private List<String> readBackupFileLines(final Uri backupFileUri) {
        final List<String> backupFileLines = new ArrayList<>();

        try {
            final InputStream restoreInputStream = getContext().getContentResolver().openInputStream(backupFileUri);

            if (restoreInputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));

                String line;

                do {
                    line = reader.readLine();

                    if (!TextUtils.isEmpty(line)) {
                        backupFileLines.add(line);
                    }
                } while (line != null);

                reader.close();
                restoreInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return backupFileLines;
    }

    @DebugLog
    private void restoreLine(String line) {
        ActiveAndroid.beginTransaction();

        try {
            final String[] values = line.split(",");
            final Day day = Day.createDateIfDoesNotExist(Day.fromDateString(values[0]));
            final DateTime date = day.getDateObject();

            // Start at 1 to skip the first header column which is "Date" and not a food
            for (int j = 1; j < headers.length; j++) {
                final Integer numServings = Integer.valueOf(values[j]);
                if (numServings > 0) {
                    Servings.createServingsIfDoesNotExist(date, getFoodByName(headers[j]), numServings);
                }
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private Food getFoodByName(String foodName) {
        if (!foodLookup.containsKey(foodName)) {
            foodLookup.put(foodName, Food.getByName(foodName));
        }

        return foodLookup.get(foodName);
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
        Common.showToast(context, success ? R.string.restore_success : R.string.restore_failed);

        if (listener != null) {
            listener.onRestoreComplete(success);
        }
    }
}
