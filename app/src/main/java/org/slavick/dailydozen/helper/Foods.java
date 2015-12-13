package org.slavick.dailydozen.helper;

import android.util.Log;

import com.activeandroid.query.Select;

import org.slavick.dailydozen.model.Food;

public class Foods {
    private static final String TAG = Foods.class.getSimpleName();

    public static void ensureAllFoodsExistInDatabase(final String[] foodNames, final int[] recommendedServings) {
        for (int i = 0; i < foodNames.length; i++) {
            createFoodIfDoesNotExist(foodNames[i], recommendedServings[i]);
        }
    }

    public static Food getFoodByName(final String foodName) {
        return new Select().from(Food.class).where("name = ?", foodName).executeSingle();
    }

    private static boolean foodExists(String foodName) {
        return new Select().from(Food.class).where("name = ?", foodName).exists();
    }

    public static void createFoodIfDoesNotExist(final String foodName, final int recommendedServings) {
        if (!foodExists(foodName)) {
            final Food food = new Food(foodName, recommendedServings);
            food.save();

            Log.d(TAG, String.format("Created %s", food));
        }
    }
}
