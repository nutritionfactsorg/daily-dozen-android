package org.slavick.dailydozen.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

@Table(name = "foods")
public class Food extends Model implements Serializable {
    private static final String TAG = Food.class.getSimpleName();

    // This points to the Id column in the Model class. This is here so the serialized Food object will have its id
    @Column(name = "Id")
    private long id;

    @Column(name = "name", index = true)
    private String name;

    @Column(name = "recommended_servings")
    private int recommendedServings;

    // Could not name this getId() as that clashes with a method in Model that is marked as final
    public long getFoodId() {
        return id;
    }

    public String getName() {
        return name;
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

    public static void ensureAllFoodsExistInDatabase(final String[] foodNames, final int[] recommendedServings) {
        for (int i = 0; i < foodNames.length; i++) {
            createFoodIfDoesNotExist(foodNames[i], recommendedServings[i]);
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
