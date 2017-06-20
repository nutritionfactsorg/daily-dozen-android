package org.nutritionfacts.dailydozen.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.enums.Units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FoodInfo {
    private static Map<String, Integer> foodImages;
    private static Map<String, Integer> foodIcons;
    private static Map<String, List<String>> servingSizesImperial;
    private static Map<String, List<String>> servingSizesMetric;
    private static Map<String, List<String>> typesOfFood;
    private static Map<String, String> foodTypeVideos;
    private static Map<String, List<String>> foodVideos;

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
        initFoodTypeVideos(resources);
        initFoodVideos(resources);
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

    private static void putTypeOfFood(Resources res, String food, int foodInfoTypeId) {
        typesOfFood.put(food, Arrays.asList(res.getStringArray(foodInfoTypeId)));
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

    public static List<String> getServingSizes(final String foodName,
                                               @Units.Interface final int unitType) {
        switch (unitType) {
            case Units.METRIC:
                return servingSizesMetric.get(foodName);
            default:
            case Units.IMPERIAL:
                return servingSizesImperial.get(foodName);
        }

    }

    private static void putServingSize(Resources res,
                                       Map<String, List<String>> map,
                                       String food,
                                       int servingSizesId) {
        map.put(food, Arrays.asList(res.getStringArray(servingSizesId)));
    }

    private static void initServingSizes(Resources res) {
        servingSizesImperial = new ArrayMap<>();
        putServingSize(res, servingSizesImperial, beans, R.array.food_info_serving_sizes_beans_imperial);
        putServingSize(res, servingSizesImperial, berries, R.array.food_info_serving_sizes_berries_imperial);
        putServingSize(res, servingSizesImperial, otherFruits, R.array.food_info_serving_sizes_other_fruits_imperial);
        putServingSize(res, servingSizesImperial, cruciferousVegetables, R.array.food_info_serving_sizes_cruciferous_vegetables_imperial);
        putServingSize(res, servingSizesImperial, greens, R.array.food_info_serving_sizes_greens_imperial);
        putServingSize(res, servingSizesImperial, otherVegetables, R.array.food_info_serving_sizes_other_vegetables_imperial);
        putServingSize(res, servingSizesImperial, flaxseeds, R.array.food_info_serving_sizes_flaxseeds_imperial);
        putServingSize(res, servingSizesImperial, nuts, R.array.food_info_serving_sizes_nuts_imperial);
        putServingSize(res, servingSizesImperial, spices, R.array.food_info_serving_sizes_spices_imperial);
        putServingSize(res, servingSizesImperial, wholeGrains, R.array.food_info_serving_sizes_whole_grains_imperial);
        putServingSize(res, servingSizesImperial, beverages, R.array.food_info_serving_sizes_beverages_imperial);
        putServingSize(res, servingSizesImperial, exercise, R.array.food_info_serving_sizes_exercise_imperial);

        servingSizesMetric = new ArrayMap<>();
        putServingSize(res, servingSizesMetric, beans, R.array.food_info_serving_sizes_beans_metric);
        putServingSize(res, servingSizesMetric, berries, R.array.food_info_serving_sizes_berries_metric);
        putServingSize(res, servingSizesMetric, otherFruits, R.array.food_info_serving_sizes_other_fruits_metric);
        putServingSize(res, servingSizesMetric, cruciferousVegetables, R.array.food_info_serving_sizes_cruciferous_vegetables_metric);
        putServingSize(res, servingSizesMetric, greens, R.array.food_info_serving_sizes_greens_metric);
        putServingSize(res, servingSizesMetric, otherVegetables, R.array.food_info_serving_sizes_other_vegetables_metric);
        putServingSize(res, servingSizesMetric, flaxseeds, R.array.food_info_serving_sizes_flaxseeds_metric);
        putServingSize(res, servingSizesMetric, nuts, R.array.food_info_serving_sizes_nuts_metric);
        putServingSize(res, servingSizesMetric, spices, R.array.food_info_serving_sizes_spices_metric);
        putServingSize(res, servingSizesMetric, wholeGrains, R.array.food_info_serving_sizes_whole_grains_metric);
        putServingSize(res, servingSizesMetric, beverages, R.array.food_info_serving_sizes_beverages_metric);
        putServingSize(res, servingSizesMetric, exercise, R.array.food_info_serving_sizes_exercise_metric);

    }

    private static void putFoodTypeVideos(Resources res, String food, int urlId) {
        foodTypeVideos.put(food, getVideoLink(res.getString(urlId)));
    }

    public static String getFoodTypeVideosLink(final String foodName) {
        return foodTypeVideos.get(foodName);
    }

    private static void initFoodTypeVideos(Resources res) {
        foodTypeVideos = new ArrayMap<>();

        putFoodTypeVideos(res, beans, R.string.food_info_videos_beans);
        putFoodTypeVideos(res, berries, R.string.food_info_videos_berries);
        putFoodTypeVideos(res, otherFruits, R.string.food_info_videos_fruits);
        putFoodTypeVideos(res, cruciferousVegetables, R.string.food_info_videos_cruciferous);
        putFoodTypeVideos(res, greens, R.string.food_info_videos_greens);
        putFoodTypeVideos(res, otherVegetables, R.string.food_info_videos_vegetables);
        putFoodTypeVideos(res, flaxseeds, R.string.food_info_videos_flaxseeds);
        putFoodTypeVideos(res, nuts, R.string.food_info_videos_nuts);
        putFoodTypeVideos(res, spices, R.string.food_info_videos_spices);
        putFoodTypeVideos(res, wholeGrains, R.string.food_info_videos_whole_grains);
        putFoodTypeVideos(res, beverages, R.string.food_info_videos_beverages);
        putFoodTypeVideos(res, exercise, R.string.food_info_videos_exercise);
    }

    public static List<String> getFoodVideosLink(final String foodName) {
        return foodVideos.containsKey(foodName) ? foodVideos.get(foodName) : new ArrayList<String>();
    }

    private static void putFoodVideosLink(Resources res, String food, int videosId) {
        final List<String> topicVideos = new ArrayList<>();

        for (String topic : res.getStringArray(videosId)) {
            topicVideos.add(getVideoLink(topic));
        }

        foodVideos.put(food, topicVideos);
    }

    private static void initFoodVideos(Resources res) {
        foodVideos = new ArrayMap<>();

        putFoodVideosLink(res, beans, R.array.food_videos_beans);
        putFoodVideosLink(res, berries, R.array.food_videos_berries);
        putFoodVideosLink(res, otherFruits, R.array.food_videos_other_fruits);
        putFoodVideosLink(res, cruciferousVegetables, R.array.food_videos_cruciferous_vegetables);
        putFoodVideosLink(res, greens, R.array.food_videos_greens);
        putFoodVideosLink(res, otherVegetables, R.array.food_videos_other_vegetables);
        // NOTE: There is only a single flaxseeds topic so we only show the top level link in the ActionBar
        putFoodVideosLink(res, nuts, R.array.food_videos_nuts);
        putFoodVideosLink(res, spices, R.array.food_videos_spices);
        putFoodVideosLink(res, wholeGrains, R.array.food_videos_whole_grains);
        putFoodVideosLink(res, beverages, R.array.food_videos_beverages);
        // NOTE: There are no topics for specific exercises
    }

    private static String getVideoLink(final String websiteTopicName) {
        return TextUtils.isEmpty(websiteTopicName) ? "" : "http://nutritionfacts.org/topics/" + websiteTopicName;
    }
}
