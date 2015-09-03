package org.nutritionfacts.dailydozen.food;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chankruse on 15-09-03.
 */
public class FoodType implements Parcelable {
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

    public Integer iconResourceId;
    public Integer overviewImageResourceId;
    public String name;
    public Double recommendedServingCount;
    public String servingExample;
    public ArrayList<String> exampleTitles;
    public List<String> exampleBodies;

    public FoodType() {
        super();

        exampleTitles = new ArrayList<>();
        exampleBodies = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeValue(iconResourceId);
        dest.writeValue(overviewImageResourceId);
        dest.writeValue(name);
        dest.writeValue(recommendedServingCount);
        dest.writeValue(servingExample);

        dest.writeStringList(exampleTitles);
        dest.writeStringList(exampleBodies);
    }

    public static final Parcelable.Creator<FoodType> CREATOR = new Parcelable.Creator<FoodType>() {
        @Override
        public FoodType createFromParcel(Parcel source) {
            FoodType foodType = new FoodType();

            foodType.iconResourceId = (Integer)source.readValue(Integer.class.getClassLoader());
            foodType.overviewImageResourceId = (Integer)source.readValue(Integer.class.getClassLoader());
            foodType.name = (String)source.readValue(String.class.getClassLoader());
            foodType.recommendedServingCount = (Double)source.readValue(Double.class.getClassLoader());
            foodType.servingExample = (String)source.readValue(String.class.getClassLoader());

            foodType.exampleTitles = new ArrayList<>();
            source.readStringList(foodType.exampleTitles);

            foodType.exampleBodies = new ArrayList<>();
            source.readStringList(foodType.exampleBodies);

            return foodType;
        }

        @Override
        public FoodType[] newArray(int size) {
            return new FoodType[size];
        }
    };
}
