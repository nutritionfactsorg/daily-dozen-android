package org.slavick.dailydozen;

import com.activeandroid.app.Application;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class DailyFoodsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
