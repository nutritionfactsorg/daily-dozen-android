package org.nutritionfacts.dailydozen;

import com.activeandroid.app.Application;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import io.fabric.sdk.android.Fabric;

public class DailyDozenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        ensureAllFoodsExistInDatabase();

        FoodInfo.init(this);

        NotificationUtil.initUpdateNotificationAlarm(this);
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
}
