package org.slavick.dailydozen;

import com.activeandroid.app.Application;
import com.crashlytics.android.Crashlytics;

import org.slavick.dailydozen.model.Food;

import io.fabric.sdk.android.Fabric;

public class DailyFoodsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        ensureAllFoodsExistInDatabase();
    }

    private void ensureAllFoodsExistInDatabase() {
        Food.ensureAllFoodsExistInDatabase(
                getResources().getStringArray(R.array.food_names),
                getResources().getIntArray(R.array.food_quantities));
    }
}
