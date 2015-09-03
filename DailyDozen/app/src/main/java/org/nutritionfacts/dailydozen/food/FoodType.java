package org.nutritionfacts.dailydozen.food;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chankruse on 15-09-03.
 */
public class FoodType {
    public static final String K_IDENTIFIER_BEANS = "K_IDENTIFIER_BEANS";
    public static final String K_IDENTIFIER_BERRIES = "K_IDENTIFIER_BERRIES";
    public static final String K_IDENTIFIER_OTHER_FRUIT = "K_IDENTIFIER_OTHER_FRUIT";
    public static final String K_IDENTIFIER_CRUCIFEROUS = "K_IDENTIFIER_CRUCIFEROUS";
    public static final String K_IDENTIFIER_GREENS = "K_IDENTIFIER_GREENS";
    public static final String K_IDENTIFIER_OTHER_VEG = "K_IDENTIFIER_OTHER_VEG";
    public static final String K_IDENTIFIER_FLAX = "K_IDENTIFIER_FLAX";
    public static final String K_IDENTIFIER_NUTS = "K_IDENTIFIER_NUTS";
    public static final String K_IDENTIFIER_SPICES = "K_IDENTIFIER_SPICES";
    public static final String K_IDENTIFIER_WHOLE_GRAINS = "K_IDENTIFIER_WHOLE_GRAINS";
    public static final String K_IDENTIFIER_BEVERAGES = "K_IDENTIFIER_BEVERAGES";
    public static final String K_IDENTIFIER_EXERCISES = "K_IDENTIFIER_EXERCISES";

    public Drawable icon;
    public String name;
    public double recommendedServingCount;
    public String servingExample;
    public List<String> exampleTitles;
    public List<String> exampleBodies;

    public FoodType() {
        super();

        exampleTitles = new ArrayList<>();
        exampleBodies = new ArrayList<>();
    }
}
