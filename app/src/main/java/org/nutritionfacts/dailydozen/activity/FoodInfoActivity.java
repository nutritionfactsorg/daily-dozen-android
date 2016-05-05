package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.FoodInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodInfoActivity extends FoodLoadingActivity {
    @Bind(R.id.food_info_image)
    protected ImageView ivFood;
    @Bind(R.id.food_types)
    protected ListView lvFoodTypes;
    @Bind(R.id.food_serving_sizes)
    protected ListView lvFoodServingSizes;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        ButterKnife.bind(this);

        displayFoodInfo();
    }

    private void displayFoodInfo() {
        final String foodName = getFood().getName();

        initImage(foodName);
        initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
        initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
    }

    private void initImage(String foodName) {
        ivFood.setImageDrawable(ContextCompat.getDrawable(this, FoodInfo.getFoodImage(foodName)));
    }

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        Common.fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }
}
