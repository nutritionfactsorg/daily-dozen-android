package org.nutritionfacts.dailydozen.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.v4.util.ArrayMap;

import org.nutritionfacts.dailydozen.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FoodInfo {
    private static Map<String, Integer> foodImages;
    private static Map<String, Integer> foodIcons;
    private static Map<String, List<String>> servingSizes;
    private static Map<String, List<String>> typesOfFood;
    
    private static String beans;
    private static String berries;
    private static String otherFruits;
    private static String cruciferousVegetables;
    private static String greens;
    private static String otherVegetables;
    private static String flaxseeds;
    private static String nuts;
    private static String spices;
    private static String wholeGrains;
    private static String beverages;
    private static String exercise;

    public static void init(final Context context) {
        final Resources resources = context.getResources();

        bindFoodNames(resources);

        initFoodImages();
        initFoodIcons();
        initServingSizes(resources);
        initTypesOfFood(resources);
    }

    private static void bindFoodNames(Resources res) {
        beans = res.getString(R.string.beans);
        berries = res.getString(R.string.berries);
        otherFruits = res.getString(R.string.other_fruits);
        cruciferousVegetables = res.getString(R.string.cruciferous_vegetables);
        greens = res.getString(R.string.greens);
        otherVegetables = res.getString(R.string.other_vegetables);
        flaxseeds = res.getString(R.string.flaxseeds);
        nuts = res.getString(R.string.nuts);
        spices = res.getString(R.string.spices);
        wholeGrains = res.getString(R.string.whole_grains);
        beverages = res.getString(R.string.beverages);
        exercise = res.getString(R.string.exercise);
    }

    @DrawableRes
    public static Integer getFoodImage(final String foodName) {
        return foodImages.get(foodName);
    }

    private static void initFoodImages() {
        foodImages = new ArrayMap<>();

        foodImages.put(beans, R.drawable.beans);
        foodImages.put(berries, R.drawable.berries);
        foodImages.put(otherFruits, R.drawable.other_fruits);
        foodImages.put(cruciferousVegetables, R.drawable.cruciferous_vegetables);
        foodImages.put(greens, R.drawable.greens);
        foodImages.put(otherVegetables, R.drawable.other_vegetables);
        foodImages.put(flaxseeds, R.drawable.flaxseeds);
        foodImages.put(nuts, R.drawable.nuts);
        foodImages.put(spices, R.drawable.spices);
        foodImages.put(wholeGrains, R.drawable.whole_grains);
        foodImages.put(beverages, R.drawable.beverages);
        foodImages.put(exercise, R.drawable.exercise);
    }

    @DrawableRes
    public static Integer getFoodIcon(final String foodName) {
        return foodIcons.get(foodName);
    }

    private static void initFoodIcons() {
        foodIcons = new ArrayMap<>();

        foodIcons.put(beans, R.drawable.ic_beans);
        foodIcons.put(berries, R.drawable.ic_berries);
        foodIcons.put(otherFruits, R.drawable.ic_other_fruits);
        foodIcons.put(cruciferousVegetables, R.drawable.ic_cruciferous_vegetables);
        foodIcons.put(greens, R.drawable.ic_greens);
        foodIcons.put(otherVegetables, R.drawable.ic_other_vegetables);
        foodIcons.put(flaxseeds, R.drawable.ic_flaxseeds);
        foodIcons.put(nuts, R.drawable.ic_nuts);
        foodIcons.put(spices, R.drawable.ic_spices);
        foodIcons.put(wholeGrains, R.drawable.ic_whole_grains);
        foodIcons.put(beverages, R.drawable.ic_beverages);
        foodIcons.put(exercise, R.drawable.ic_exercise);
    }

    public static List<String> getTypesOfFood(final String foodName) {
        return typesOfFood.get(foodName);
    }

    private static void initTypesOfFood(Resources res) {
        typesOfFood = new ArrayMap<>();

        putTypeOfFood(res, beans, R.array.food_info_types_beans);
        putTypeOfFood(res, berries, R.array.food_info_types_berries);
        putTypeOfFood(res, otherFruits, R.array.food_info_types_other_fruits);
        putTypeOfFood(res, cruciferousVegetables, R.array.food_info_types_cruciferous_vegetables);
        putTypeOfFood(res, greens, R.array.food_info_types_greens);
        putTypeOfFood(res, otherVegetables, R.array.food_info_types_other_vegetables);
        putTypeOfFood(res, flaxseeds, R.array.food_info_types_flaxseeds);
        putTypeOfFood(res, nuts, R.array.food_info_types_nuts);
        putTypeOfFood(res, spices, R.array.food_info_types_spices);
        putTypeOfFood(res, wholeGrains, R.array.food_info_types_whole_grains);
        putTypeOfFood(res, beverages, R.array.food_info_types_beverages);
        putTypeOfFood(res, exercise, R.array.food_info_types_exercise);
    }

    private static void putTypeOfFood(Resources res, String food, int foodInfoTypeId) {
        typesOfFood.put(food, Arrays.asList(res.getStringArray(foodInfoTypeId)));
    }

    public static List<String> getServingSizes(final String foodName) {
        return servingSizes.get(foodName);
    }

    private static void initServingSizes(Resources res) {
        servingSizes = new ArrayMap<>();

        putServingSize(res, beans, R.array.food_info_serving_sizes_beans);
        putServingSize(res, berries, R.array.food_info_serving_sizes_berries);
        putServingSize(res, otherFruits, R.array.food_info_serving_sizes_other_fruits);
        putServingSize(res, cruciferousVegetables, R.array.food_info_serving_sizes_cruciferous_vegetables);
        putServingSize(res, greens, R.array.food_info_serving_sizes_greens);
        putServingSize(res, otherVegetables, R.array.food_info_serving_sizes_other_vegetables);
        putServingSize(res, flaxseeds, R.array.food_info_serving_sizes_flaxseeds);
        putServingSize(res, nuts, R.array.food_info_serving_sizes_nuts);
        putServingSize(res, spices, R.array.food_info_serving_sizes_spices);
        putServingSize(res, wholeGrains, R.array.food_info_serving_sizes_whole_grains);
        putServingSize(res, beverages, R.array.food_info_serving_sizes_beverages);
        putServingSize(res, exercise, R.array.food_info_serving_sizes_exercise);
    }

    private static void putServingSize(Resources res, String food, int servingSizesId) {
        servingSizes.put(food, Arrays.asList(res.getStringArray(servingSizesId)));
    }
}
