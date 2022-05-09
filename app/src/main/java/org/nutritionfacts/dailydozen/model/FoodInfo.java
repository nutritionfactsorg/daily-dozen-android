package org.nutritionfacts.dailydozen.model;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.collection.ArrayMap;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.enums.Units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FoodInfo {
    private static Map<String, Integer> foodImages;
    private static Map<String, Integer> foodIcons;
    private static Map<String, Integer> tweakIcons;
    private static Map<String, Integer> tweakImages;
    private static Map<String, String> tweakShorts;
    private static Map<String, String> tweakTexts;
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
    private static String vitaminB12;

    private static String tweakMealWater;
    private static String tweakMealNegCal;
    private static String tweakMealVinegar;
    private static String tweakMealUndistracted;
    private static String tweakMealTwentyMinutes;
    private static String tweakDailyBlackCumin;
    private static String tweakDailyGarlic;
    private static String tweakDailyGinger;
    private static String tweakDailyNutriYeast;
    private static String tweakDailyCumin;
    private static String tweakDailyGreenTea;
    private static String tweakDailyHydrate;
    private static String tweakDailyDeflourDiet;
    private static String tweakDailyFrontLoad;
    private static String tweakDailyTimeRestrict;
    private static String tweakExerciseTiming;
    private static String tweakWeighTwice;
    private static String tweakCompleteIntentions;
    private static String tweakNightlyFast;
    private static String tweakNightlySleep;
    private static String tweakNightlyTrendelenburg;

    public static void init(final Context context) {
        final Resources res = context.getResources();

        bindFoodNames(res);
        bindTweakNames(res);

        initFoodImages();
        initFoodIcons();
        initTweakIcons();
        initTweakImages();
        initTweakShorts(res);
        initTweakTexts(res);
        initServingSizes(context);
        initTypesOfFood(res);
        initFoodTypeVideos(res);
        initFoodVideos(res);
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
        vitaminB12 = res.getString(R.string.vitamin_b12);
    }

    private static void bindTweakNames(Resources res) {
        tweakMealWater = res.getString(R.string.meal_water);
        tweakMealNegCal = res.getString(R.string.meal_negcal);
        tweakMealVinegar = res.getString(R.string.meal_vinegar);
        tweakMealUndistracted = res.getString(R.string.meal_undistracted);
        tweakMealTwentyMinutes = res.getString(R.string.meal_twentyminutes);
        tweakDailyBlackCumin = res.getString(R.string.daily_blackcumin);
        tweakDailyGarlic = res.getString(R.string.daily_garlic);
        tweakDailyGinger = res.getString(R.string.daily_ginger);
        tweakDailyNutriYeast = res.getString(R.string.daily_nutriyeast);
        tweakDailyCumin = res.getString(R.string.daily_cumin);
        tweakDailyGreenTea = res.getString(R.string.daily_greentea);
        tweakDailyHydrate = res.getString(R.string.daily_hydrate);
        tweakDailyDeflourDiet = res.getString(R.string.daily_deflourdiet);
        tweakDailyFrontLoad = res.getString(R.string.daily_frontload);
        tweakDailyTimeRestrict = res.getString(R.string.daily_timerestrict);
        tweakExerciseTiming = res.getString(R.string.exercise_timing);
        tweakWeighTwice = res.getString(R.string.weigh_twice);
        tweakCompleteIntentions = res.getString(R.string.complete_intentions);
        tweakNightlyFast = res.getString(R.string.nightly_fast);
        tweakNightlySleep = res.getString(R.string.nightly_sleep);
        tweakNightlyTrendelenburg = res.getString(R.string.nightly_trendelenburg);
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
        foodIcons.put(vitaminB12, R.drawable.ic_vitamin_b12);
    }

    @DrawableRes
    public static Integer getTweakIcon(final String tweakName) {
        return tweakIcons.get(tweakName);
    }

    @DrawableRes
    public static Integer getTweakImage(final String tweakName) {
        return tweakImages.get(tweakName);
    }

    public static String getTweakShort(final String tweakName) {
        return tweakShorts.get(tweakName);
    }

    public static String getTweakText(final String tweakName) {
        return tweakTexts.get(tweakName);
    }

    private static void initTweakIcons() {
        tweakIcons = new ArrayMap<>();

        tweakIcons.put(tweakMealWater, R.drawable.ic_meal_water);
        tweakIcons.put(tweakMealNegCal, R.drawable.ic_meal_negcal);
        tweakIcons.put(tweakMealVinegar, R.drawable.ic_meal_vinegar);
        tweakIcons.put(tweakMealUndistracted, R.drawable.ic_meal_undistracted);
        tweakIcons.put(tweakMealTwentyMinutes, R.drawable.ic_meal_20_minutes);
        tweakIcons.put(tweakDailyBlackCumin, R.drawable.ic_daily_black_cumin);
        tweakIcons.put(tweakDailyGarlic, R.drawable.ic_daily_garlic);
        tweakIcons.put(tweakDailyGinger, R.drawable.ic_daily_ginger);
        tweakIcons.put(tweakDailyNutriYeast, R.drawable.ic_daily_nutriyeast);
        tweakIcons.put(tweakDailyCumin, R.drawable.ic_daily_cumin);
        tweakIcons.put(tweakDailyGreenTea, R.drawable.ic_daily_green_tea);
        tweakIcons.put(tweakDailyHydrate, R.drawable.ic_daily_hydrate);
        tweakIcons.put(tweakDailyDeflourDiet, R.drawable.ic_daily_deflour_diet);
        tweakIcons.put(tweakDailyFrontLoad, R.drawable.ic_daily_front_load);
        tweakIcons.put(tweakDailyTimeRestrict, R.drawable.ic_daily_time_restrict);
        tweakIcons.put(tweakExerciseTiming, R.drawable.ic_exercise_timing);
        tweakIcons.put(tweakWeighTwice, R.drawable.ic_weigh_twice);
        tweakIcons.put(tweakCompleteIntentions, R.drawable.ic_complete_intentions);
        tweakIcons.put(tweakNightlyFast, R.drawable.ic_nightly_fast);
        tweakIcons.put(tweakNightlySleep, R.drawable.ic_nightly_sleep);
        tweakIcons.put(tweakNightlyTrendelenburg, R.drawable.ic_nightly_trendelenburg);
    }

    private static void initTweakImages() {
        tweakImages = new ArrayMap<>();

        tweakImages.put(tweakMealWater, R.drawable.meal_water_image);
        tweakImages.put(tweakMealNegCal, R.drawable.meal_negcal_image);
        tweakImages.put(tweakMealVinegar, R.drawable.mean_vinegar_image);
        tweakImages.put(tweakMealUndistracted, R.drawable.meal_undistracted_image);
        tweakImages.put(tweakMealTwentyMinutes, R.drawable.meal_20_minutes_image);
        tweakImages.put(tweakDailyBlackCumin, R.drawable.daily_black_cumin_image);
        tweakImages.put(tweakDailyGarlic, R.drawable.daily_garlic_image);
        tweakImages.put(tweakDailyGinger, R.drawable.daily_ginger_image);
        tweakImages.put(tweakDailyNutriYeast, R.drawable.daily_nutriyeast_image);
        tweakImages.put(tweakDailyCumin, R.drawable.daily_cumin_image);
        tweakImages.put(tweakDailyGreenTea, R.drawable.daily_green_tea_image);
        tweakImages.put(tweakDailyHydrate, R.drawable.daily_hydrate_image);
        tweakImages.put(tweakDailyDeflourDiet, R.drawable.daily_deflour_diet_image);
        tweakImages.put(tweakDailyFrontLoad, R.drawable.daily_front_load_image);
        tweakImages.put(tweakDailyTimeRestrict, R.drawable.daily_time_restrict_image);
        tweakImages.put(tweakExerciseTiming, R.drawable.exercise_timing_image);
        tweakImages.put(tweakWeighTwice, R.drawable.weigh_twice_image);
        tweakImages.put(tweakCompleteIntentions, R.drawable.complete_intentions_image);
        tweakImages.put(tweakNightlyFast, R.drawable.nightly_fast_image);
        tweakImages.put(tweakNightlySleep, R.drawable.nightly_sleep_image);
        // Do not have an image for the following tweak, so reusing an appropriate image
        tweakImages.put(tweakNightlyTrendelenburg, R.drawable.nightly_sleep_image);
    }

    private static void initTweakShorts(final Resources res) {
        tweakShorts = new ArrayMap<>();

        tweakShorts.put(tweakMealWater, res.getString(R.string.meal_water_short));
        tweakShorts.put(tweakMealNegCal, res.getString(R.string.meal_negcal_short));
        tweakShorts.put(tweakMealVinegar, res.getString(R.string.meal_vinegar_short));
        tweakShorts.put(tweakMealUndistracted, res.getString(R.string.meal_undistracted_short));
        tweakShorts.put(tweakMealTwentyMinutes, res.getString(R.string.meal_twentyminutes_short));
        tweakShorts.put(tweakDailyBlackCumin, res.getString(R.string.daily_blackcumin_short));
        tweakShorts.put(tweakDailyGarlic, res.getString(R.string.daily_garlic_short));
        tweakShorts.put(tweakDailyGinger, res.getString(R.string.daily_ginger_short));
        tweakShorts.put(tweakDailyNutriYeast, res.getString(R.string.daily_nutriyeast_short));
        tweakShorts.put(tweakDailyCumin, res.getString(R.string.daily_cumin_short));
        tweakShorts.put(tweakDailyGreenTea, res.getString(R.string.daily_greentea_short));
        tweakShorts.put(tweakDailyHydrate, res.getString(R.string.daily_hydrate_short));
        tweakShorts.put(tweakDailyDeflourDiet, res.getString(R.string.daily_deflourdiet_short));
        tweakShorts.put(tweakDailyFrontLoad, res.getString(R.string.daily_frontload_short));
        tweakShorts.put(tweakDailyTimeRestrict, res.getString(R.string.daily_timerestrict_short));
        tweakShorts.put(tweakExerciseTiming, res.getString(R.string.exercise_timing_short));
        tweakShorts.put(tweakWeighTwice, res.getString(R.string.weigh_twice_short));
        tweakShorts.put(tweakCompleteIntentions, res.getString(R.string.complete_intentions_short));
        tweakShorts.put(tweakNightlyFast, res.getString(R.string.nightly_fast_short));
        tweakShorts.put(tweakNightlySleep, res.getString(R.string.nightly_sleep_short));
        tweakShorts.put(tweakNightlyTrendelenburg, res.getString(R.string.nightly_trendelenburg_short));
    }

    private static void initTweakTexts(final Resources res) {
        tweakTexts = new ArrayMap<>();

        tweakTexts.put(tweakMealWater, res.getString(R.string.meal_water_text));
        tweakTexts.put(tweakMealNegCal, res.getString(R.string.meal_negcal_text));
        tweakTexts.put(tweakMealVinegar, res.getString(R.string.meal_vinegar_text));
        tweakTexts.put(tweakMealUndistracted, res.getString(R.string.meal_undistracted_text));
        tweakTexts.put(tweakMealTwentyMinutes, res.getString(R.string.meal_twentyminutes_text));
        tweakTexts.put(tweakDailyBlackCumin, res.getString(R.string.daily_blackcumin_text));
        tweakTexts.put(tweakDailyGarlic, res.getString(R.string.daily_garlic_text));
        tweakTexts.put(tweakDailyGinger, res.getString(R.string.daily_ginger_text));
        tweakTexts.put(tweakDailyNutriYeast, res.getString(R.string.daily_nutriyeast_text));
        tweakTexts.put(tweakDailyCumin, res.getString(R.string.daily_cumin_text));
        tweakTexts.put(tweakDailyGreenTea, res.getString(R.string.daily_greentea_text));
        tweakTexts.put(tweakDailyHydrate, res.getString(R.string.daily_hydrate_text));
        tweakTexts.put(tweakDailyDeflourDiet, res.getString(R.string.daily_deflourdiet_text));
        tweakTexts.put(tweakDailyFrontLoad, res.getString(R.string.daily_frontload_text));
        tweakTexts.put(tweakDailyTimeRestrict, res.getString(R.string.daily_timerestrict_text));
        tweakTexts.put(tweakExerciseTiming, res.getString(R.string.exercise_timing_text));
        tweakTexts.put(tweakWeighTwice, res.getString(R.string.weigh_twice_text));
        tweakTexts.put(tweakCompleteIntentions, res.getString(R.string.complete_intentions_text));
        tweakTexts.put(tweakNightlyFast, res.getString(R.string.nightly_fast_text));
        tweakTexts.put(tweakNightlySleep, res.getString(R.string.nightly_sleep_text));
        tweakTexts.put(tweakNightlyTrendelenburg, res.getString(R.string.nightly_trendelenburg_text));
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

    private static int getServingSizesResourceId(final Context context,
                                                 final String foodName,
                                                 @Units.Interface final int unitType) {
        return context.getResources().getIdentifier(
                "food_info_serving_sizes_" + (foodName + (unitType == Units.IMPERIAL ? "_imperial" : "_metric")).toLowerCase(),
                "array",
                context.getApplicationInfo().packageName);
    }

    private static void initServingSizes(Context context) {
        servingSizesImperial = new ArrayMap<>();
        servingSizesMetric = new ArrayMap<>();

        for (Food food : Food.getAllFoods()) {
            initServingSizesForFood(context, food.getIdName());
        }
    }

    private static void initServingSizesForFood(final Context context, final String foodIdName) {
        final Resources res = context.getResources();

        // Convert food name to resource id pattern ("Other Vegetables" becomes "other_vegetables")
        final String formattedFoodIdName = foodIdName.toLowerCase().replace(" ", "_");

        try {
            // Dynamically load the string-arrays for food.
            // The naming convention below must be followed:
            //      food_info_serving_sizes_<formattedFoodIdName>_imperial
            //      food_info_serving_sizes_<formattedFoodIdName>_metric
            servingSizesImperial.put(foodIdName,
                    Arrays.asList(res.getStringArray(getServingSizesResourceId(context, formattedFoodIdName, Units.IMPERIAL))));
            servingSizesMetric.put(foodIdName,
                    Arrays.asList(res.getStringArray(getServingSizesResourceId(context, formattedFoodIdName, Units.METRIC))));
        } catch (Resources.NotFoundException e) {
            // Vitamin B12 doesn't need the above functionality and therefore doesn't have the
            // required resource ids for the above code to function correctly
        }
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
        putFoodTypeVideos(res, vitaminB12, R.string.food_info_videos_vitamin_b12);
    }

    public static List<String> getFoodVideosLink(final String foodName) {
        return foodVideos.containsKey(foodName) ? foodVideos.get(foodName) : new ArrayList<>();
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
