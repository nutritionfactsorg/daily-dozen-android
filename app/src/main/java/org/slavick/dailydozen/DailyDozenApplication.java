package org.slavick.dailydozen;

import com.activeandroid.app.Application;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.slavick.dailydozen.controller.Prefs;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.FoodInfo;
import org.slavick.dailydozen.task.CalculateStreaksTask;

import io.fabric.sdk.android.Fabric;

public class DailyDozenApplication extends Application implements CalculateStreaksTask.Listener {
    @Override
    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        executeOneTimeTasks();

        FoodInfo.init(this);
    }

    private void executeOneTimeTasks() {
        ensureAllFoodsExistInDatabase();

        calculateStreaksAfterDatabaseUpgradeToV2();
    }

    private void ensureAllFoodsExistInDatabase() {
        final Prefs prefs = Prefs.getInstance(this);
        if (!prefs.foodsHaveBeenCreated()) {
            Food.ensureAllFoodsExistInDatabase(
                    getResources().getStringArray(R.array.food_names),
                    getResources().getIntArray(R.array.food_quantities));

            prefs.setFoodsHaveBeenCreated();
        }
    }

    private void calculateStreaksAfterDatabaseUpgradeToV2() {
        if (!Prefs.getInstance(this).streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2()) {
            new CalculateStreaksTask(this, this).execute();
        }
    }

    @Override
    public void onCalculateStreaksComplete(boolean success) {
        if (success) {
            Prefs.getInstance(this).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
        }
    }
}
