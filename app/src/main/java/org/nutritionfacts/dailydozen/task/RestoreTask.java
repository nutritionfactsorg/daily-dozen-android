package org.nutritionfacts.dailydozen.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.collection.ArrayMap;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class RestoreTask extends TaskWithContext<Uri, Integer, Boolean> {
    private String[] headers;
    private ArrayMap<String, Food> foodLookup;

    public RestoreTask(Context context) {
        super(context);
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
        try {
            final ContentResolver contentResolver = getContext().getContentResolver();

            if (params != null && params.length > 0) {
                InputStream restoreInputStream = contentResolver.openInputStream(params[0]);

                if (restoreInputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));

                    final LineNumberReader lineNumberReader = new LineNumberReader(reader);
                    lineNumberReader.skip(Integer.MAX_VALUE);
                    final int numLines = lineNumberReader.getLineNumber() + 1;
                    lineNumberReader.close();

                    // Need to recreate the InputStream and BufferedReader after closing LineNumberReader
                    final InputStream inputStream = contentResolver.openInputStream(params[0]);

                    if (inputStream != null) {
                        // Only delete all existing data if we are sure we have an input stream
                        deleteAllExistingData();

                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line = reader.readLine();
                        if (line != null) {
                            headers = line.split(",");

                            int i = 0;

                            do {
                                if (!isCancelled()) {
                                    line = reader.readLine();

                                    if (!TextUtils.isEmpty(line)) {
                                        restoreLine(line);
                                    }

                                    publishProgress(++i, numLines);
                                }
                            } while (line != null);
                        }

                        reader.close();
                        restoreInputStream.close();
                    }

                    return !isCancelled();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @DebugLog
    private void deleteAllExistingData() {
        DDServings.truncate(DDServings.class);
        Day.truncate(Day.class);
    }

    @DebugLog
    private void restoreLine(String line) {
        ActiveAndroid.beginTransaction();

        try {
            final String[] values = line.split(",");

            if (values.length > 0) {
                final Day day = Day.createDayIfDoesNotExist(values[0]);

                // Start at 1 to skip the first header column which is "Date" and not a food
                for (int j = 1; j < headers.length; j++) {
                    final int numServings = Integer.parseInt(values[j]);
                    if (numServings > 0) {
                        final Food food = getFoodByName(headers[j]);
                        if (food != null) {
                            DDServings.createServingsIfDoesNotExist(day, food, numServings);
                        }
                    }
                }

                ActiveAndroid.setTransactionSuccessful();
            }
        } catch (InvalidDateException e) {
            Timber.e(e, "restoreLine: ");
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private Food getFoodByName(String foodName) {
        if (!foodLookup.containsKey(foodName)) {
            foodLookup.put(foodName, Food.getByNameOrIdName(foodName));
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
        Bus.restoreCompleteEvent(success);
    }
}
