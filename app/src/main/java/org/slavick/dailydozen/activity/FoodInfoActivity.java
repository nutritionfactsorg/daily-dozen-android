package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.FoodInfo;

import java.util.List;

public class FoodInfoActivity extends AppCompatActivity {
    protected ListView lvFoodTypes;
    protected ListView lvFoodServingSizes;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        lvFoodTypes = (ListView) findViewById(R.id.food_types);
        lvFoodServingSizes = (ListView) findViewById(R.id.food_serving_sizes);

        initActionBar();

        displayInfoForFood();
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayInfoForFood() {
        final Intent intent = getIntent();
        if (intent != null) {
            final long foodId = intent.getLongExtra(Args.FOOD_ID, -1);
            final String foodName = intent.getStringExtra(Args.FOOD_NAME);
            final int recommendedServings = intent.getIntExtra(Args.FOOD_RECOMMENDED_SERVINGS, 1);

            setTitle(foodName);

            initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
            initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
        }
    }

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        Common.fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }
}
