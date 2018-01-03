package org.nutritionfacts.dailydozen;

import com.activeandroid.app.Application;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

public class DailyDozenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());

        CustomLogger.init(this);

        ensureAllFoodsExistInDatabase();

        FoodInfo.initFoodIcons(this);

        NotificationUtil.initUpdateReminderNotificationAlarm(this);
    }

    private void ensureAllFoodsExistInDatabase() {
        Food.ensureAllFoodsExistInDatabase(
                getResources().getStringArray(R.array.food_names),
                getResources().getStringArray(R.array.food_id_names),
                getResources().getIntArray(R.array.food_quantities));
    }
}
