package org.nutritionfacts.dailydozen;

import android.app.Application;
import android.util.Log;

import org.nutritionfacts.dailydozen.data.DataManager;
import org.nutritionfacts.dailydozen.food.FoodHelper;
import org.nutritionfacts.dailydozen.user.UserManager;
import org.nutritionfacts.dailydozen.util.SharedPreferenceHelper;

/**
 * Created by chankruse on 15-09-02.
 */
public class DailyDozenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initializeSingletons();
    }

    private void initializeSingletons() {

        SharedPreferenceHelper.initializeInstance(getApplicationContext());
        FoodHelper.getInstance().setContext(getApplicationContext());
        UserManager.getInstance().setContext(getApplicationContext());
        UserManager.getInstance().setContext(getApplicationContext());
        if (UserManager.getInstance().hasUserRegistered()) {
            UserManager.getInstance().initializeUserDatabase();
            DataManager.getInstance().incrementUsageCount();
        }
    }
}
