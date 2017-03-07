package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Servings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodServings extends LinearLayout {
    private Day day;
    private Food food;

    @BindView(R.id.food_icon)
    protected ImageView ivIcon;
    @BindView(R.id.food_name)
    protected TextView tvName;
    @BindView(R.id.food_streak)
    protected StreakWidget tvStreak;
    @BindView(R.id.food_checkboxes)
    protected FoodCheckBoxes foodCheckBoxes;

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
        final View view = LayoutInflater.from(context).inflate(R.layout.food_servings, this);
        ButterKnife.bind(this, view);
    }

    public boolean setDateAndFood(final Day day, final Food food) {
        this.day = day;
        this.food = food;

        final boolean foundFoodIcon = initFoodIcon();
        if (foundFoodIcon) {
            initFoodName();

            final Servings servings = getServings();
            initCheckboxes(servings);
            initFoodStreak(servings);

            return true;
        } else {
            return false;
        }
    }

    private Servings getServings() {
        return Servings.getByDateAndFood(day, food);
    }

    private boolean initFoodIcon() {
        final Context context = getContext();
        return Common.loadImage(context, ivIcon, FoodInfo.getFoodIcon(food.getName()));
    }

    private void initFoodName() {
        tvName.setText(String.format("%s %s", food.getName(), getContext().getString(R.string.icon_info)));
    }

    private void initFoodStreak(Servings servings) {
        final int streak = servings != null ? servings.getStreak() : 0;
        if (streak > 0) {
            tvStreak.setVisibility(VISIBLE);
            tvStreak.setStreak(streak);
        } else {
            tvStreak.setVisibility(GONE);
        }
    }

    @OnClick({R.id.food_icon, R.id.food_name})
    public void onFoodNameClicked() {
        Common.openFoodInfo(getContext(), food);
    }

    @OnClick({R.id.food_history, R.id.food_streak})
    public void onFoodHistoryClicked() {
        Common.openFoodHistory(getContext(), food);
    }

    private void initCheckboxes(Servings servings) {
        foodCheckBoxes.setDay(day);
        foodCheckBoxes.setFood(food);
        foodCheckBoxes.setServings(servings);
    }

    @Subscribe
    public void onEvent(FoodServingsChangedEvent event) {
        if (event.getFoodName().equals(food.getName())) {
            initFoodStreak(getServings());
        }
    }
}
