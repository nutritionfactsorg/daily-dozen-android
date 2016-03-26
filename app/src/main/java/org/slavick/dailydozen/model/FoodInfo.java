package org.slavick.dailydozen.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.util.ArrayMap;

import org.slavick.dailydozen.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FoodInfo {
    private static Map<String, Integer> foodIcons;
    private static Map<String, List<String>> servingSizes;
    private static Map<String, List<String>> typesOfFood;

    public static void init(final Context context) {
        final Resources resources = context.getResources();

        initFoodIcons(resources);
        initServingSizes(resources);
        initTypesOfFood(resources);
    }

    public static Integer getFoodIcon(final String foodName) {
        return foodIcons.get(foodName);
    }

    private static void initFoodIcons(Resources res) {
        foodIcons = new ArrayMap<>();

        foodIcons.put(res.getString(R.string.beans), R.drawable.ic_beans);
        foodIcons.put(res.getString(R.string.berries), R.drawable.ic_berries);
        foodIcons.put(res.getString(R.string.other_fruits), R.drawable.ic_other_fruits);
        foodIcons.put(res.getString(R.string.cruciferous_vegetables), R.drawable.ic_cruciferous_vegetables);
        foodIcons.put(res.getString(R.string.greens), R.drawable.ic_greens);
        foodIcons.put(res.getString(R.string.other_vegetables), R.drawable.ic_other_vegetables);
        foodIcons.put(res.getString(R.string.flaxseeds), R.drawable.ic_flaxseeds);
        foodIcons.put(res.getString(R.string.nuts), R.drawable.ic_nuts);
        foodIcons.put(res.getString(R.string.spices), R.drawable.ic_spices);
        foodIcons.put(res.getString(R.string.whole_grains), R.drawable.ic_whole_grains);
        foodIcons.put(res.getString(R.string.beverages), R.drawable.ic_beverages);
        foodIcons.put(res.getString(R.string.exercise), R.drawable.ic_exercise);
    }

    public static List<String> getTypesOfFood(final String foodName) {
        return typesOfFood.get(foodName);
    }

    private static void initTypesOfFood(Resources res) {
        typesOfFood = new ArrayMap<>();

        typesOfFood.put(res.getString(R.string.beans), Arrays.asList(
                "Black beans",
                "Black-eyed peas",
                "Butter beans",
                "Cannellini beans",
                "Chickpeas",
                "Edamame",
                "English peas",
                "Garbanzo beans",
                "Great northern beans",
                "Kidney beans",
                "Lentils (beluga, french, and red varieties)",
                "Miso",
                "Navy beans",
                "Pinto beans",
                "Small red beans",
                "Split peas (yellow or green)",
                "Tempeh"
        ));

        typesOfFood.put(res.getString(R.string.berries), Arrays.asList(
                "Acai berries",
                "Barberries",
                "Blackberries",
                "Blueberries",
                "Cherries (sweet or tart)",
                "Concord grapes",
                "Cranberries",
                "Goji berries",
                "Kumquats",
                "Mulberries",
                "Raspberries (black or red)",
                "Strawberries"
        ));

        typesOfFood.put(res.getString(R.string.other_fruits), Arrays.asList(
                "Apples",
                "Dried apricots",
                "Avocados",
                "Bananas",
                "Cantaloupe",
                "Clementines",
                "Dates",
                "Dried figs",
                "Grapefruit",
                "Honeydew",
                "Kiwifruit",
                "Lemons",
                "Limes",
                "Lychees",
                "Mangos",
                "Nectarines",
                "Oranges",
                "Papaya",
                "Passion fruit",
                "Peaches",
                "Pears",
                "Pineapple",
                "Plums (especially black plums)",
                "Pluots",
                "Pomegranates",
                "Prunes",
                "Tangerines",
                "Watermelon"
        ));

        typesOfFood.put(res.getString(R.string.cruciferous_vegetables), Arrays.asList(
                "Arugula",
                "Bok choy",
                "Broccoli",
                "Brussels sprouts",
                "Cabbage",
                "Cauliflower",
                "Collard greens",
                "Horseradish",
                "Kale (black, green, and red)",
                "Mustard greens",
                "Radishes",
                "Turnip greens",
                "Watercress"
        ));

        typesOfFood.put(res.getString(R.string.greens), Arrays.asList(
                "Arugula",
                "Beet greens",
                "Collard greens",
                "Kale (black, green, and red)",
                "Mesclun mix (assorted young salad greens)",
                "Mustard greens",
                "Sorrel",
                "Spinach",
                "Swiss chard",
                "Turnip greens"
        ));

        typesOfFood.put(res.getString(R.string.other_vegetables), Arrays.asList(
                "Artichokes",
                "Asparagus",
                "Beets",
                "Bell peppers",
                "Carrots",
                "Corn",
                "Garlic",
                "Mushrooms (button, oyster, portobello, and shiitake)",
                "Okra",
                "Onions",
                "Purple potatoes",
                "Pumpkin",
                "Sea vegetables (arame, dulse, and nori)",
                "Snap peas",
                "Squash (delicata, summer, and spaghetti squash varieties)",
                "Sweet potatoes/yams",
                "Tomatoes",
                "Zucchini"
        ));

        typesOfFood.put(res.getString(R.string.flaxseeds), Arrays.asList(
                "Brown flaxseeds",
                "Golden flaxseeds"
        ));

        typesOfFood.put(res.getString(R.string.nuts), Arrays.asList(
                "Almonds",
                "Brazil nuts",
                "Cashews",
                "Chia seeds",
                "Hazelnuts",
                "Hemp seeds",
                "Macadamia nuts",
                "Pecans",
                "Pistachios",
                "Pumpkin seeds",
                "Sesame seeds",
                "Sunflower seeds",
                "Walnuts"
        ));

        typesOfFood.put(res.getString(R.string.spices), Arrays.asList(
                "Allspice",
                "Barberries",
                "Basil",
                "Bay leaves",
                "Cardamom",
                "Chili powder",
                "Cilantro",
                "Cinnamon",
                "Cloves",
                "Coriander",
                "Cumin",
                "Curry powder",
                "Dill",
                "Fenugreek",
                "Garlic",
                "Ginger",
                "Horseradish",
                "Lemongrass",
                "Marjoram",
                "Mustard powder",
                "Nutmeg",
                "Oregano",
                "Smoked paprika",
                "Parsley",
                "Pepper",
                "Peppermint",
                "Rosemary",
                "Saffron",
                "Sage",
                "Thyme",
                "Turmeric",
                "Vanilla"
        ));

        typesOfFood.put(res.getString(R.string.whole_grains), Arrays.asList(
                "Barley",
                "Brown rice",
                "Buckwheat",
                "Millet",
                "Oats",
                "Popcorn",
                "Quinoa",
                "Rye",
                "Teff",
                "Whole-wheat pasta",
                "Wild rice"
        ));

        typesOfFood.put(res.getString(R.string.beverages), Arrays.asList(
                "Black tea",
                "Chai tea",
                "Vanilla chamomile tea",
                "Coffee",
                "Earl grey tea",
                "Green tea",
                "Hibiscus tea",
                "Hot chocolate",
                "Jasmine tea",
                "Lemon balm tea",
                "Matcha tea",
                "Almond blossom oolong tea",
                "Peppermint tea",
                "Rooibos tea", // is this spelled correctly?
                "Water",
                "White tea"
        ));

        typesOfFood.put(res.getString(R.string.exercise), Arrays.asList(
                "Bicycling",
                "Canoeing",
                "Dancing",
                "Dodgeball",
                "Downhill skiing",
                "Fencing",
                "Hiking",
                "Housework",
                "Ice-skating",
                "Inline skating",
                "Juggling",
                "Jumping on a trampoline",
                "Paddle boating",
                "Playing frisbee",
                "Roller-skating",
                "Shooting baskets",
                "Shoveling light snow",
                "Skateboarding",
                "Snorkeling",
                "Surfing",
                "Swimming recreationally",
                "Tennis (doubles)",
                "Treading water",
                "Walking briskly (4 mph)",
                "Water aerobics",
                "Waterskiing",
                "Yard work",
                "Yoga",

                "Backpacking",
                "Basketball",
                "Bicycling uphill",
                "Circuit weight training",
                "Cross-country skiing",
                "Football",
                "Hockey",
                "Jogging",
                "Jumping jacks",
                "Jumping rope",
                "Lacrosse",
                "Push-ups",
                "Pull-ups",
                "Racquetball",
                "Rock climbing",
                "Rugby",
                "Running",
                "Scuba diving",
                "Tennis (singles)",
                "Soccer",
                "Speed skating",
                "Squash",
                "Step aerobics",
                "Swimming laps",
                "Walking briskly uphill",
                "Water jogging"
        ));
    }

    public static List<String> getServingSizes(final String foodName) {
        return servingSizes.get(foodName);
    }

    private static void initServingSizes(Resources res) {
        servingSizes = new ArrayMap<>();

        servingSizes.put(res.getString(R.string.beans), Arrays.asList(
                "1/4 cup of hummus or bean dip",
                "1/2 cup cooked beans, split peas, lentils, tofu, or tempeh",
                "1 cup of fresh peas or sprouted lentils"
        ));

        servingSizes.put(res.getString(R.string.berries), Arrays.asList(
                "1/2 cup fresh or frozen",
                "1/4 cup dried"
        ));

        servingSizes.put(res.getString(R.string.other_fruits), Arrays.asList(
                "1 medium-sized fruit",
                "1 cup cut-up fruit",
                "1/4 cup dried fruit"
        ));

        servingSizes.put(res.getString(R.string.cruciferous_vegetables), Arrays.asList(
                "1/2 cup chopped",
                "1/4 cup Brussels or broccoli sprouts",
                "1 tablespoon horseradish"
        ));

        servingSizes.put(res.getString(R.string.greens), Arrays.asList(
                "1 cup raw",
                "1/2 cup cooked"
        ));

        servingSizes.put(res.getString(R.string.other_vegetables), Arrays.asList(
                "1 cup raw leafy vegetables",
                "1/2 cup raw or cooked nonleafy vegetables",
                "1/2 cup vegetable juice",
                "1/4 cup dried mushrooms"
        ));

        servingSizes.put(res.getString(R.string.flaxseeds), Collections.singletonList(
                "1 tablespoon ground"
        ));

        servingSizes.put(res.getString(R.string.nuts), Arrays.asList(
                "1/4 cup nuts or seeds",
                "2 tablespoons nut or seed butter"
        ));

        servingSizes.put(res.getString(R.string.spices), Arrays.asList(
                "1/4 teaspoon of turmeric",
                "Any other (salt-free) herbs and spices you enjoy"
        ));

        servingSizes.put(res.getString(R.string.whole_grains), Arrays.asList(
                "1/2 cup hot cereal or cooked grains, pasta, or corn kernels",
                "1 cup cold cereal",
                "1 tortilla or slice of bread",
                "1/2 a bagel or english muffin",
                "3 cups popped popcorn"
        ));

        servingSizes.put(res.getString(R.string.beverages), Collections.singletonList(
                "One glass (12 ounces)"
        ));

        servingSizes.put(res.getString(R.string.exercise), Arrays.asList(
                "90 minutes of moderate-intensity activity",
                "40 minutes of vigorous-intensity activity"
        ));
    }
}
