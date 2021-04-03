package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.FoodServingsBinding;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;

public class FoodServings extends LinearLayout {
    private Day day;
    private Food food;

    private FoodServingsBinding binding;

    public FoodServings(Context context) {
        super(context);
        init(context);
    }

    public FoodServings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FoodServings(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        binding = FoodServingsBinding.inflate(LayoutInflater.from(context), this, true);
        onFoodNameClicked();
        onFoodHistoryClicked();
    }

    public boolean setDateAndFood(final Day day, final Food food) {
        this.day = day;
        this.food = food;

        final boolean foundFoodIcon = initFoodIcon();
        if (foundFoodIcon) {
            initFoodName();

            final DDServings servings = getServings();
            initCheckboxes(servings);
            initFoodStreak(servings);

            return true;
        } else {
            return false;
        }
    }

    private DDServings getServings() {
        return DDServings.getByDateAndFood(day, food);
    }

    private boolean initFoodIcon() {
        return Common.loadImage(getContext(), binding.foodIcon, FoodInfo.getFoodIcon(food.getName()));
    }

    private void initFoodName() {
        binding.foodName.setText(String.format("%s %s", food.getName(), getContext().getString(R.string.icon_info)));
    }

    private void initFoodStreak(DDServings servings) {
        final int streak = servings != null ? servings.getStreak() : 0;
        if (streak > 0) {
            binding.foodStreak.setVisibility(VISIBLE);
            binding.foodStreak.setStreak(streak);
        } else {
            binding.foodStreak.setVisibility(GONE);
        }
    }

    public void onFoodNameClicked() {
        binding.foodIcon.setOnClickListener(v -> Common.openFoodInfo(getContext(), food));
        binding.foodName.setOnClickListener(v -> Common.openFoodInfo(getContext(), food));
    }

    public void onFoodHistoryClicked() {
        binding.foodHistory.setOnClickListener(v -> Common.openFoodHistory(getContext(), food));
        binding.foodStreak.setOnClickListener(v -> Common.openFoodHistory(getContext(), food));
    }

    private void initCheckboxes(DDServings servings) {
        binding.foodCheckboxes.setDay(day);
        binding.foodCheckboxes.setRDA(food);
        binding.foodCheckboxes.setServings(servings);
    }

    @Subscribe
    public void onEvent(FoodServingsChangedEvent event) {
        if (event.getFoodName().equals(food.getName())) {
            initFoodStreak(getServings());
        }
    }
}
