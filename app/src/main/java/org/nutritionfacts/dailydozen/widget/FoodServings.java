package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.FoodHistoryActivity;
import org.nutritionfacts.dailydozen.activity.FoodInfoActivity;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.view.ServingCheckBox;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.task.CalculateStreakTask;
import org.nutritionfacts.dailydozen.task.StreakTaskInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoodServings extends LinearLayout implements CalculateStreakTask.Listener {
    private final static String TAG = FoodServings.class.getSimpleName();

    private Day day;
    private Food food;
    private final List<ServingCheckBox> checkBoxes = new ArrayList<>();
    @BindView(R.id.food_icon)
    protected ImageView ivIcon;
    @BindView(R.id.food_name)
    protected TextView tvName;
    @BindView(R.id.food_streak)
    protected StreakWidget tvStreak;
    @BindView(R.id.food_checkboxes)
    protected ViewGroup vgCheckboxes;

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
        final View view = LayoutInflater.from(context).inflate(R.layout.food_item, this);
        ButterKnife.bind(this, view);
    }

    public void setDateAndFood(final Day day, final Food food) {
        this.day = day;
        this.food = food;

        initFoodIcon();
        initFoodName();

        final Servings servings = getServings();
        initCheckboxes(servings);
        initFoodStreak(servings);
    }

    private Servings getServings() {
        return Servings.getByDateAndFood(day, food);
    }

    private void initFoodIcon() {
        Common.loadImage(getContext(), ivIcon, FoodInfo.getFoodIcon(food.getName()));
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

    @OnClick(R.id.food_name)
    public void onFoodNameClicked() {
        getContext().startActivity(createFoodIntent(FoodInfoActivity.class, food));
    }

    @OnClick(R.id.food_history)
    public void onFoodHistoryClicked() {
        getContext().startActivity(createFoodIntent(FoodHistoryActivity.class, food));
    }

    private Intent createFoodIntent(final Class<? extends AppCompatActivity> klass, final Food food) {
        final Intent intent = new Intent(getContext(), klass);
        intent.putExtra(Args.FOOD_ID, food.getId());
        return intent;
    }

    private void initCheckboxes(Servings servings) {
        int numExistingServings = servings != null ? servings.getServings() : 0;

        createCheckBox(checkBoxes, numExistingServings, food.getRecommendedServings());
        vgCheckboxes.removeAllViews();

        for (CheckBox checkBox : checkBoxes) {
            vgCheckboxes.addView(checkBox);
        }
    }

    private ServingCheckBox createCheckBox(List<ServingCheckBox> checkBoxes, Integer currentServings,  Integer maxServings) {
        final ServingCheckBox checkBox = new ServingCheckBox(getContext());
        checkBox.setChecked(currentServings > 0);
        checkBox.setCheckChangeListener();
        checkBox.setOnCheckedChangeListener(getOnCheckedChangeListener(checkBox));
        if (maxServings > 1)
            checkBox.setNextServing(createCheckBox(checkBoxes, --currentServings, --maxServings));
        checkBoxes.add(checkBox);
        return checkBox;
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener(final ServingCheckBox checkBox) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.onCheckClick(isChecked);
                if (isChecked) {
                    handleServingChecked();
                } else {
                    handleServingUnchecked();
                }
            }
        };
    }

    private Integer getNumberOfCheckedBoxes(){
        Integer numChecked = 0;
        for (ServingCheckBox checkbox : checkBoxes){
            if (checkbox.isChecked())
                numChecked++;
        }
        return numChecked;
    }

    private void handleServingChecked() {
        day = Day.createDayIfDoesNotExist(day);

        final Servings servings = Servings.createServingsIfDoesNotExist(day, food);
        if (servings != null) {
            while (getNumberOfCheckedBoxes() > servings.getServings())
                servings.increaseServings();

            servings.save();
            onServingsChanged();
            Log.d(TAG, String.format("Increased Servings for %s", servings));
        }
    }

    private void handleServingUnchecked() {
        final Servings servings = getServings();
        if (servings != null) {
            while (getNumberOfCheckedBoxes() < servings.getServings())
                servings.decreaseServings();

            if (servings.getServings() > 0) {
                servings.save();
                Log.d(TAG, String.format("Decreased Servings for %s", servings));
            } else {
                Log.d(TAG, String.format("Deleting %s", servings));
                servings.delete();
            }

            onServingsChanged();
        }
    }

    private void onServingsChanged() {
        new CalculateStreakTask(getContext(), this).execute(new StreakTaskInput(day, food));
    }

    @Override
    public void onCalculateStreakComplete(boolean success) {
        Bus.foodServingsChangedEvent(day, food);
    }

    @Subscribe
    public void onEvent(FoodServingsChangedEvent event) {
        if (event.getFoodName().equals(food.getName())) {
            initFoodStreak(getServings());
        }
    }
}
