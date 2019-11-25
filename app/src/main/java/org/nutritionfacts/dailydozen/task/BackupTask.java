package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.google.gson.Gson;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class BackupTask extends TaskWithContext<File, Integer, Boolean> {
    private List<Day> allDays;
    private List<Food> allFoods;
    private List<Tweak> allTweaks;

    public BackupTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (DDServings.isEmpty()) {
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
        allTweaks = Tweak.getAllTweaks();

        final File backupFile = params[0];
        Timber.d("backupFilename = %s", backupFile.getName());

        final int numDays = allDays.size();

        final String lineSeparator = Common.getLineSeparator();

        final StringBuilder jsonLines = new StringBuilder();

        for (int i = 0; i < numDays; i++) {
            if (!isCancelled()) {
                jsonLines.append(String.format("%s%s", lineSeparator, getDayJsonLine(allDays.get(i))));

                publishProgress(i + 1, numDays);
            }
        }

        if (!isCancelled()) {
            try {
                final FileWriter fileWriter = new FileWriter(backupFile);
                fileWriter.write(jsonLines.toString());
                fileWriter.close();

                Timber.d("backup file successfully written");
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
    private String getDayJsonLine(Day day) {
        final DayEntries dayEntries = new DayEntries();
        dayEntries.setDate(day.getDateString());
        dayEntries.setWeights(Weights.getWeightsOnDay(day));
        dayEntries.setDailyDozen(createFoodServingsLookup(day));
        dayEntries.setTweaks(createTweakServingsLookup(day));
        return new Gson().toJson(dayEntries);
    }

    @NonNull
    private Map<String, Integer> createFoodServingsLookup(Day day) {
        Map<String, Integer> foodServingsMap = new ArrayMap<>();

        final Set<Food> missingFoodsOnDay = new HashSet<>(allFoods);

        for (DDServings servings : DDServings.getServingsOnDate(day)) {
            foodServingsMap.put(servings.getFood().getIdName(), servings.getServings());
            missingFoodsOnDay.remove(servings.getFood());
        }

        for (Food missedFood : missingFoodsOnDay) {
            foodServingsMap.put(missedFood.getIdName(), 0);
        }

        return foodServingsMap;
    }

    @NonNull
    private Map<String, Integer> createTweakServingsLookup(Day day) {
        Map<String, Integer> tweakServingsMap = new ArrayMap<>();

        final Set<Tweak> missingTweaksOnDay = new HashSet<>(allTweaks);

        for (TweakServings servings : TweakServings.getServingsOnDate(day)) {
            tweakServingsMap.put(servings.getTweak().getIdName(), servings.getServings());
            missingTweaksOnDay.remove(servings.getTweak());
        }

        for (Tweak missedTweak : missingTweaksOnDay) {
            tweakServingsMap.put(missedTweak.getIdName(), 0);
        }

        return tweakServingsMap;
    }
}
