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

    private final ArrayMap<String, Food> foodLookup;
    private final ArrayMap<String, Tweak> tweakLookup;

    private final ArrayMap<String, Integer> streakLookup;
    private Day previousDay = null;

    public RestoreTask(ProgressListener progressListener, Uri restoreFileUri, ContentResolver contentResolver) {
        this.progressListener = progressListener;
        this.restoreFileUri = restoreFileUri;
        this.contentResolver = contentResolver;
        foodLookup = new ArrayMap<>();
        tweakLookup = new ArrayMap<>();
        streakLookup = new ArrayMap<>();
    }

    @Override
    public Boolean call() {
        try {
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
                    int i = 0;
                    while (line != null) {
                        restoreLineJSON(line);
                        TaskRunner.updateProgress(progressListener, ++i, numLines);
                        line = reader.readLine();
                    }

                    reader.close();
                    restoreInputStream.close();
                }

                return true;
            }
        } catch (IOException e) {
            Timber.e(e, "restore failed");
        }

        return false;
    }

    @Override
    public void setUiForLoading() {
        progressListener.showProgressBar(R.string.task_restore_title);
    }

    @Override
    public void setDataAfterLoading(Boolean success) {
        Bus.restoreCompleteEvent(success);
        progressListener.hideProgressBar();
    }

    private void deleteAllExistingData() {
        Common.truncateAllDatabaseTables();
    }

    private void restoreLineJSON(final String line) {
        if (TextUtils.isEmpty(line)) {
            return;
        }

        ActiveAndroid.beginTransaction();

        try {
            DayEntries dayEntries = new Gson().fromJson(line, DayEntries.class);

            final Day day = Day.createDay(dayEntries.getDate());

            // all streaks reset if days are not consecutive (indicative of skipped day)
            boolean resetStreaks = previousDay != null && !day.isOneDayAfter(previousDay);

            if (dayEntries.getMorningWeight() != 0 || dayEntries.getEveningWeight() != 0) {
                Weights.createWeights(day, dayEntries.getMorningWeight(), dayEntries.getEveningWeight());
            }

            for (Map.Entry<String, Integer> entry : dayEntries.getDailyDozen().entrySet()) {
                final String name = entry.getKey();
                final Integer servings = entry.getValue();

                Food food = getFoodByIdName(name);

                if (resetStreaks || servings < food.getRecommendedAmount()) {
                    resetStreak(name);
                } else {
                    increaseStreak(name);
                }

                if (servings > 0) {
                    DDServings.createServingsWithStreak(day, food, servings, getStreak(name));
                }
            }

            for (Map.Entry<String, Integer> entry : dayEntries.getTweaks().entrySet()) {
                final String name = entry.getKey();
                final Integer servings = entry.getValue();

                Tweak tweak = getTweakByIdName(name);

                if (resetStreaks || servings < tweak.getRecommendedAmount()) {
                    resetStreak(name);
                } else {
                    increaseStreak(name);
                }

                if (servings > 0) {
                    TweakServings.createServingsWithStreak(day, tweak, servings, getStreak(name));
                }
            }

            ActiveAndroid.setTransactionSuccessful();

            previousDay = day;
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

    private Integer getStreak(String name) {
        if (!streakLookup.containsKey(name)) {
            resetStreak(name);
        }
        return streakLookup.get(name);
    }

    private void increaseStreak(String name) {
        streakLookup.put(name, getStreak(name) + 1);
    }

    private void resetStreak(String name) {
        streakLookup.put(name, 0);
    }
}
