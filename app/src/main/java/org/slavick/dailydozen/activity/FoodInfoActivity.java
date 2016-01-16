package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.FoodInfo;

import java.util.List;

public class FoodInfoActivity extends FoodLoadingActivity {
    protected ListView lvFoodTypes;
    protected ListView lvFoodServingSizes;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        lvFoodTypes = (ListView) findViewById(R.id.food_types);
        lvFoodServingSizes = (ListView) findViewById(R.id.food_serving_sizes);

        displayFoodInfo();
    }

    private void displayFoodInfo() {
        final String foodName = getFood().getName();

        initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
        initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
    }

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        Common.fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }
}
