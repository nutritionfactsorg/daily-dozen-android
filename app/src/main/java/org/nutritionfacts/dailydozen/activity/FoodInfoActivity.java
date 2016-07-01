package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.FoodServingsAdapter;
import org.nutritionfacts.dailydozen.adapter.FoodTypeAdapter;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodInfoActivity extends FoodLoadingActivity {
    private final static String TAG = FoodInfoActivity.class.getSimpleName();

    @BindView(R.id.food_info_image)
    protected ImageView ivFood;
    @BindView(R.id.food_serving_sizes)
    protected RecyclerView lvFoodServingSizes;
    @BindView(R.id.food_types)
    protected RecyclerView lvFoodTypes;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        ButterKnife.bind(this);

        displayFoodInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.food_info_videos:
                openVideosInBrowser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayFoodInfo() {
        final Food food = getFood();

        if (food != null && !TextUtils.isEmpty(food.getName())) {
            final String foodName = food.getName();

            initImage(foodName);
            initServingTypes(foodName);
            initFoodTypes(foodName);
        }
    }

    private void initImage(String foodName) {
        Common.loadImage(this, ivFood, FoodInfo.getFoodImage(foodName));
    }

    private void initServingTypes(String foodName) {
        final List<String> servingSizes = FoodInfo.getServingSizes(foodName);
        final FoodServingsAdapter adapter = new FoodServingsAdapter(servingSizes);

        initList(lvFoodServingSizes, adapter);
    }

    private void initFoodTypes(String foodName) {
        final List<String> foods = FoodInfo.getTypesOfFood(foodName);
        final List<String> videos = FoodInfo.getFoodVideosLink(foodName);
        final RecyclerView.Adapter adapter = new FoodTypeAdapter(foods, videos);

        initList(lvFoodTypes, adapter);
    }

    private void initList(final RecyclerView list, final RecyclerView.Adapter adapter) {
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void openVideosInBrowser() {
        final Food food = getFood();

        if (food != null && !TextUtils.isEmpty(food.getName())) {
            Common.openUrlInExternalBrowser(this, FoodInfo.getFoodTypeVideosLink(food.getName()));
        }
    }
}
