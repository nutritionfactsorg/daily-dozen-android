package org.nutritionfacts.dailydozen.task;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;

import androidx.collection.ArrayMap;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.DayEntries;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.model.Weights;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;

import timber.log.Timber;

public class RestoreTask extends BaseTask<Boolean> {
    private final ProgressListener progressListener;
    private final Uri restoreFileUri;
    private final ContentResolver contentResolver;

    private String[] headers;
    private ArrayMap<String, Food> foodLookup;
    private ArrayMap<String, Tweak> tweakLookup;

    public RestoreTask(ProgressListener progressListener, Uri restoreFileUri, ContentResolver contentResolver) {
        this.progressListener = progressListener;
        this.restoreFileUri = restoreFileUri;
        this.contentResolver = contentResolver;
        foodLookup = new ArrayMap<>();
        tweakLookup = new ArrayMap<>();
    }

    @Override
    public Boolean call() {
        try {
            final String restoreFileType = contentResolver.getType(restoreFileUri);
            InputStream restoreInputStream = contentResolver.openInputStream(restoreFileUri);

            if (restoreInputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));

                final LineNumberReader lineNumberReader = new LineNumberReader(reader);
                lineNumberReader.skip(Integer.MAX_VALUE);
                final int numLines = lineNumberReader.getLineNumber() + 1;
                lineNumberReader.close();

                // Need to recreate the InputStream and BufferedReader after closing LineNumberReader
                final InputStream inputStream = contentResolver.openInputStream(restoreFileUri);

                if (inputStream != null) {
                    // Only delete all existing data if we are sure we have an input stream
                    deleteAllExistingData();

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line = reader.readLine();
                    if (line != null) {
                        int i = 0;

                        if ("text/csv".equals(restoreFileType)) {
                            headers = line.split(",");

                            do {
                                line = reader.readLine();
                                restoreLineCSV(headers, line);
                                progressListener.updateProgressBar(++i, numLines);
                            } while (line != null);
                        } else {
                            do {
                                line = reader.readLine();
                                restoreLineJSON(line);
                                progressListener.updateProgressBar(++i, numLines);
                            } while (line != null);
                        }
                    }

                    reader.close();
                    restoreInputStream.close();
                }

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void setUiForLoading() {
        progressListener.showProgressBar(R.string.task_restore_title);
    }

    @Override
    public void setDataAfterLoading(Boolean success) {
        progressListener.hideProgressBar();

        Bus.restoreCompleteEvent(success);
    }

    private void deleteAllExistingData() {
        Common.truncateAllDatabaseTables();
    }

    private void restoreLineCSV(final String[] headers, final String line) {
        if (TextUtils.isEmpty(line)) {
            return;
        }

        ActiveAndroid.beginTransaction();

        try {
            final String[] values = line.split(",");

            if (values.length > 0) {
                final Day day = Day.createDayIfDoesNotExist(values[0]);

                // Start at 1 to skip the first header column which is "Date" and not a food
                for (int j = 1; j < headers.length; j++) {
                    final int numServings = Integer.parseInt(values[j]);
                    if (numServings > 0) {
                        final Food food = getFoodByIdName(headers[j]);
                        if (food != null) {
                            DDServings.createServingsAndRecalculateStreak(day, food, numServings);
                        }
                    }
                }

                ActiveAndroid.setTransactionSuccessful();
            }
        } catch (InvalidDateException e) {
            Timber.e(e, "restoreLineCSV: ");
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void restoreLineJSON(final String line) {
        if (TextUtils.isEmpty(line)) {
            return;
        }

        ActiveAndroid.beginTransaction();

        try {
            DayEntries dayEntries = new Gson().fromJson(line, DayEntries.class);

            final Day day = Day.createDayIfDoesNotExist(dayEntries.getDate());

            Weights.createWeightsIfDoesNotExist(day,
                    dayEntries.getMorningWeight(),
                    dayEntries.getEveningWeight());

            for (Map.Entry<String, Integer> entry : dayEntries.getDailyDozen().entrySet()) {
                DDServings.createServingsAndRecalculateStreak(day, getFoodByIdName(entry.getKey()), entry.getValue());
            }

            for (Map.Entry<String, Integer> entry : dayEntries.getTweaks().entrySet()) {
                TweakServings.createServingsIfDoesNotExist(day, getTweakByIdName(entry.getKey()), entry.getValue());
            }

            ActiveAndroid.setTransactionSuccessful();
        } catch (InvalidDateException e) {
            Timber.e(e, "restoreLineJSON: ");
        } catch (JsonSyntaxException e) {
            Timber.e(e, "restoreLineJSON: ");
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private Food getFoodByIdName(String foodIdName) {
        if (!foodLookup.containsKey(foodIdName)) {
            foodLookup.put(foodIdName, Food.getByNameOrIdName(foodIdName));
        }

        return foodLookup.get(foodIdName);
    }

    private Tweak getTweakByIdName(String tweakIdName) {
        if (!tweakLookup.containsKey(tweakIdName)) {
            tweakLookup.put(tweakIdName, Tweak.getByNameOrIdName(tweakIdName));
        }

        return tweakLookup.get(tweakIdName);
    }
}
