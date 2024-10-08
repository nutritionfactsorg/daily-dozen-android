package org.nutritionfacts.dailydozen.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.FoodServingsAdapter;
import org.nutritionfacts.dailydozen.adapter.FoodTypeAdapter;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.ActivityFoodInfoBinding;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.enums.Units;

import java.util.List;

import timber.log.Timber;

public class FoodInfoActivity extends InfoActivity {
    private ActivityFoodInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFood() == null) {
            Timber.e("Could not open activity: getFood returned null");
            finish();
            return;
        }

        binding = ActivityFoodInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onChangeUnitsClicked();

        displayFoodInfo();

        // Don't show the change units button when displaying info for exercise
        if (Common.EXERCISE.equalsIgnoreCase(getFood().getIdName())) {
            binding.changeUnitsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.food_info_videos) {
            openVideosInBrowser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFoodInfo() {
        final Food food = getFood();

        if (food != null && !TextUtils.isEmpty(food.getName())) {
            final String foodName = food.getName();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                initImage(foodName);
                binding.foodInfoImage.setVisibility(View.VISIBLE);
            } else {
                binding.foodInfoImage.setVisibility(View.GONE);
            }
            initServingTypes(food);
            initFoodTypes(foodName);
        }
    }

    private void initImage(String foodName) {
        Common.loadImage(this, binding.foodInfoImage, FoodInfo.getFoodImage(foodName));
    }

    private void initServingTypes(final Food food) {
        final List<String> servingSizes = FoodInfo.getServingSizes(food.getIdName(),
                Prefs.getInstance(this).getUnitTypePref());

        initChangeUnitsButton();
        binding.foodServingSizes.setAdapter(new FoodServingsAdapter(servingSizes));
        binding.foodServingSizes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initChangeUnitsButton() {
        binding.changeUnitsButton.setText(Prefs.getInstance(this).getUnitTypePref() == Units.IMPERIAL ?
                R.string.imperial : R.string.metric);
    }

    private void initFoodTypes(String foodName) {
        final List<String> foods = FoodInfo.getTypesOfFood(foodName);
        final List<String> videos = FoodInfo.getFoodVideosLink(foodName);

        binding.foodTypes.setAdapter(new FoodTypeAdapter(foods, videos));
        binding.foodTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void openVideosInBrowser() {
        final Food food = getFood();

        if (food != null && !TextUtils.isEmpty(food.getName())) {
            Common.openUrlInExternalBrowser(this, FoodInfo.getFoodTypeVideosLink(food.getName()));
        }
    }

    public void onChangeUnitsClicked() {
        binding.changeUnitsButton.setOnClickListener(v -> {
            Prefs.getInstance(v.getContext()).toggleUnitType();

            initServingTypes(getFood());
        });
    }
}
