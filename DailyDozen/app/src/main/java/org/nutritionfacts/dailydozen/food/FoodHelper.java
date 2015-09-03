package org.nutritionfacts.dailydozen.food;

import android.content.Context;
import android.content.res.Resources;

import org.nutritionfacts.dailydozen.R;

/**
 * Created by chankruse on 15-09-03.
 */
public class FoodHelper {
    private Context context; //Use application context

    private static FoodHelper ourInstance = new FoodHelper();

    public static FoodHelper getInstance() {
        return ourInstance;
    }

    private FoodHelper() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FoodType getFoodTypeForIdentifier(String identifier) {

        FoodType foodType = new FoodType();

        Resources resources = context.getResources();

        if (identifier.equals(FoodType.K_IDENTIFIER_BEANS)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_beans_name);
            foodType.recommendedServingCount = 3.0;
            foodType.servingExample = resources.getString(R.string.food_type_beans_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_beans_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_BERRIES)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_berries_name);
            foodType.recommendedServingCount = 1.0;
            foodType.servingExample = resources.getString(R.string.food_type_berries_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_berries_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_OTHER_FRUIT)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_other_fruit_name);
            foodType.recommendedServingCount = 3.0;
            foodType.servingExample = resources.getString(R.string.food_type_other_fruit_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_other_fruit_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_CRUCIFEROUS)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_cruciferous_name);
            foodType.recommendedServingCount = 1.0;
            foodType.servingExample = resources.getString(R.string.food_type_cruciferous_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_cruciferous_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_GREENS)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_greens_name);
            foodType.recommendedServingCount = 2.0;
            foodType.servingExample = resources.getString(R.string.food_type_greens_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_greens_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_OTHER_VEG)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_other_vegetables_name);
            foodType.recommendedServingCount = 2.0;
            foodType.servingExample = resources.getString(R.string.food_type_other_vegetables_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_other_vegetables_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_FLAX)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_flax_seeds_name);
            foodType.recommendedServingCount = 1.0;
            foodType.servingExample = resources.getString(R.string.food_type_flax_seeds_serving);

        } else if (identifier.equals(FoodType.K_IDENTIFIER_NUTS)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_nuts_name);
            foodType.recommendedServingCount = 1.0;
            foodType.servingExample = resources.getString(R.string.food_type_nuts_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_nuts_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_SPICES)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_spices_name);
            foodType.recommendedServingCount = -1.0;
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_spices_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_WHOLE_GRAINS)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_whole_grains_name);
            foodType.recommendedServingCount = 3.0;
            foodType.servingExample = resources.getString(R.string.food_type_whole_grains_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_whole_grains_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_BEVERAGES)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_beverages_name);
            foodType.recommendedServingCount = 5.0;
            foodType.servingExample = resources.getString(R.string.food_type_beverages_serving);
            foodType.exampleTitles.add(resources.getString(R.string.my_favorites));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_beverages_example));

        } else if (identifier.equals(FoodType.K_IDENTIFIER_EXERCISES)) {
            foodType.icon = resources.getDrawable(R.drawable.ic_test);
            foodType.name = resources.getString(R.string.food_type_exercise_name);
            foodType.recommendedServingCount = 1.0;
            foodType.servingExample = resources.getString(R.string.food_type_exercise_serving);
            foodType.exampleTitles.add(resources.getString(R.string.examples_mod_activity));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_exercise_example));
            foodType.exampleTitles.add(resources.getString(R.string.examples_vig_activity));
            foodType.exampleBodies.add(resources.getString(R.string.food_type_exercise_example_2));

        }

        return foodType;
    }
}
