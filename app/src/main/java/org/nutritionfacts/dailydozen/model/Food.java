package org.nutritionfacts.dailydozen.model;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import hugo.weaving.DebugLog;

@Table(name = "foods")
public class Food extends Model {
    private static final String TAG = Food.class.getSimpleName();

    @Column(name = "name", index = true)
    private String name;

    @Column(name = "id_name", index = true)
    private String idName;

    @Column(name = "recommended_servings")
    private int recommendedServings;

    public String getName() {
        return name;
    }

    public String getIdName() {
        return idName;
    }

    public int getRecommendedServings() {
        return recommendedServings;
    }

    public Food() {
    }

    public Food(String name, int recommendedServings) {
        this.name = name;
        this.recommendedServings = recommendedServings;
    }

    @Override
    public String toString() {
        return name;
    }

    @DebugLog
    public static void ensureAllFoodsExistInDatabase(final String[] foodNames, final int[] recommendedServings) {
        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < foodNames.length; i++) {
                createFoodIfDoesNotExist(foodNames[i], recommendedServings[i]);
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Food getById(long foodId) {
        return new Select().from(Food.class).where("Id = ?", foodId).executeSingle();
    }

    public static Food getByName(final String foodName) {
        return new Select().from(Food.class).where("name = ?", foodName).executeSingle();
    }

    private static boolean exists(String foodName) {
        return getByName(foodName) != null;
    }

    public static void createFoodIfDoesNotExist(final String foodName, final int recommendedServings) {
        if (!exists(foodName)) {
            final Food food = new Food(foodName, recommendedServings);
            food.save();

            Log.d(TAG, String.format("Created %s", food));
        }
    }

    public static List<Food> getAllFoods() {
        return new Select().from(Food.class).execute();
    }
}
