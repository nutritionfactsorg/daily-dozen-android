package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.FoodInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodInfoActivity extends AppCompatActivity {
    @Bind(R.id.food_types)
    protected ListView lvFoodTypes;

    @Bind(R.id.food_serving_sizes)
    protected ListView lvFoodServingSizes;

    private String foodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        ButterKnife.bind(this);

        displayInfoForFood();
    }

    private void displayInfoForFood() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Args.FOOD_NAME)) {
            foodName = intent.getStringExtra(Args.FOOD_NAME);

            lvFoodTypes.setAdapter(createAdapter(FoodInfo.getTypesOfFood(foodName)));

            lvFoodServingSizes.setAdapter(createAdapter(FoodInfo.getServingSizes(foodName)));
        }
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }
}
