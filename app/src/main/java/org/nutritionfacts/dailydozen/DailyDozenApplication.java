package org.nutritionfacts.dailydozen;

import com.activeandroid.app.Application;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import timber.log.Timber;

public class DailyDozenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        ensureAllFoodsExistInDatabase();

        FoodInfo.init(this);

        NotificationUtil.init(this);
    }

    private void ensureAllFoodsExistInDatabase() {
        Food.ensureAllFoodsExistInDatabase(
                getResources().getStringArray(R.array.food_names),
                getResources().getStringArray(R.array.food_id_names),
                getResources().getIntArray(R.array.food_quantities));
    }
}
