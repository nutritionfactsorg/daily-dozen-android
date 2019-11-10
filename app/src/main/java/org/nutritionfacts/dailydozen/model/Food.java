package org.nutritionfacts.dailydozen.model;

import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.nutritionfacts.dailydozen.RDA;

import java.util.List;

import hugo.weaving.DebugLog;

@Table(name = "foods")
public class Food extends Model implements RDA {
    @Column(name = "name", index = true)
    private String name;

    @Column(name = "id_name", index = true)
    private String idName;

    @Column(name = "recommended_servings")
    private int recommendedServings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdName() {
        return idName;
    }

    private void setIdName(String idName) {
        this.idName = idName;
    }

    public int getRecommendedAmount() {
        return recommendedServings;
    }

    public Food() {
    }

    public Food(String name, String foodIdName, int recommendedServings) {
        this.name = name;
        this.idName = foodIdName;
        this.recommendedServings = recommendedServings;
    }

    @Override
    public String toString() {
        return name;
    }

    @DebugLog
    public static void ensureAllFoodsExistInDatabase(final String[] foodNames,
                                                     final String[] foodIdNames,
                                                     final int[] recommendedServings) {
        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < foodNames.length; i++) {
                createFoodIfDoesNotExist(foodNames[i], foodIdNames[i], recommendedServings[i]);
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Food getById(long foodId) {
        return new Select().from(Food.class).where("Id = ?", foodId).executeSingle();
    }

    public static Food getByNameOrIdName(final String idName) {
        return new Select().from(Food.class)
                .where("id_name = ?", idName)
                .or("name = ?", idName)
                .executeSingle();
    }

    private static void createFoodIfDoesNotExist(final String foodName,
                                                 final String idName,
                                                 final int recommendedServings) {
        boolean needToSave = false;

        Food food = getByNameOrIdName(idName);

        if (food == null) {
            food = new Food(foodName, idName, recommendedServings);
            needToSave = true;
        } else if (TextUtils.isEmpty(food.getIdName())) {
            food.setIdName(idName);
            needToSave = true;
        }

        if (!food.getName().equalsIgnoreCase(foodName)) {
            food.setName(foodName);
            needToSave = true;
        }

        if (needToSave) {
            food.save();
        }
    }

    public static List<Food> getAllFoods() {
        return new Select().from(Food.class).execute();
    }
}
