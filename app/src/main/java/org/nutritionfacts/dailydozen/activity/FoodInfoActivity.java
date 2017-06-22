package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.FoodServingsAdapter;
import org.nutritionfacts.dailydozen.adapter.FoodTypeAdapter;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.enums.Units;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodInfoActivity extends FoodLoadingActivity {
    @BindView(R.id.food_info_image)
    protected ImageView ivFood;
    @BindView(R.id.change_units_container)
    protected ViewGroup vgChangeUnits;
    @BindView(R.id.change_units_button)
    protected Button btnChangeUnits;
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

        FoodInfo.initFoodInfo(this);

        displayFoodInfo();

        // Don't show the change units button when displaying info for exercise
        if (getFood().getIdName().equalsIgnoreCase("exercise")) {
            vgChangeUnits.setVisibility(View.GONE);
        }
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
            initServingTypes(food);
            initFoodTypes(foodName);
        }
    }

    private void initImage(String foodName) {
        Common.loadImage(this, ivFood, FoodInfo.getFoodImage(foodName));
    }

    private void initServingTypes(final Food food) {
        final List<String> servingSizes = FoodInfo.getServingSizes(food.getIdName(),
                Prefs.getInstance(this).getUnitTypePref());
        final FoodServingsAdapter adapter = new FoodServingsAdapter(servingSizes);

        initChangeUnitsButton();
        initList(lvFoodServingSizes, adapter);
    }

    private void initChangeUnitsButton() {
        btnChangeUnits.setText(Prefs.getInstance(this).getUnitTypePref() == Units.IMPERIAL ?
                R.string.imperial : R.string.metric);
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

    @OnClick(R.id.change_units_button)
    public void onChangeUnitsClicked() {
        Prefs.getInstance(this).toggleUnitType();

        initServingTypes(getFood());
    }
}
