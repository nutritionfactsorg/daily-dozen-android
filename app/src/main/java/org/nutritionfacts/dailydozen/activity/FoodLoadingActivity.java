package org.nutritionfacts.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.model.Food;

public abstract class FoodLoadingActivity extends AppCompatActivity {
    private Food food;

    public Food getFood() {
        return food;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActionBar();

        loadFoodFromIntent();

        if (food == null) {
            finish();
        }
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

    private void loadFoodFromIntent() {
        final Intent intent = getIntent();
        if (intent != null) {
            food = Food.getById(intent.getLongExtra(Args.FOOD_ID, -1));

            if (food != null) {
                setTitle(food.getName());
            }
        }
    }
}
