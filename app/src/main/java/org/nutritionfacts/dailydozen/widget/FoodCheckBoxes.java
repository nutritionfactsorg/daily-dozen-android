package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.task.CalculateStreakTask;
import org.nutritionfacts.dailydozen.task.StreakTaskInput;
import org.nutritionfacts.dailydozen.view.ServingCheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodCheckBoxes extends LinearLayout {
    private final static String TAG = FoodCheckBoxes.class.getSimpleName();

    @BindView(R.id.food_check_boxes_container)
    protected ViewGroup vgContainer;

    private List<ServingCheckBox> checkBoxes;

    private Food food;
    private Day day;

    public FoodCheckBoxes(Context context) {
        this(context, null);
    }

    public FoodCheckBoxes(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoodCheckBoxes(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.food_check_boxes, this);
        ButterKnife.bind(this);
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public void setServings(final Servings servings) {
        checkBoxes = new ArrayList<>();
        createCheckBox(
                checkBoxes,
                servings != null ? servings.getServings() : 0,
                food.getRecommendedServings());

        vgContainer.removeAllViews();

        for (ServingCheckBox checkBox : checkBoxes) {
            vgContainer.addView(checkBox);
        }
    }

    private ServingCheckBox createCheckBox(List<ServingCheckBox> checkBoxes, Integer currentServings, Integer maxServings) {
        final ServingCheckBox checkBox = new ServingCheckBox(getContext());
        checkBox.setChecked(currentServings > 0);
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
                checkBox.onCheckChange(isChecked);
                if (isChecked) {
                    handleServingChecked();
                } else {
                    handleServingUnchecked();
                }
            }
        };
    }

    private Integer getNumberOfCheckedBoxes() {
        Integer numChecked = 0;
        for (ServingCheckBox checkbox : checkBoxes) {
            if (checkbox.isChecked()) {
                numChecked++;
            }
        }
        return numChecked;
    }

    private void handleServingChecked() {
        day = Day.createDayIfDoesNotExist(day);

        final Servings servings = Servings.createServingsIfDoesNotExist(day, food);
        final Integer numberOfCheckedBoxes = getNumberOfCheckedBoxes();

        if (servings != null && servings.getServings() != numberOfCheckedBoxes) {
            servings.setServings(numberOfCheckedBoxes);

            servings.save();
            onServingsChanged();
            Log.d(TAG, String.format("Increased Servings for %s", servings));
        }
    }

    private void handleServingUnchecked() {
        final Servings servings = getServings();
        final Integer numberOfCheckedBoxes = getNumberOfCheckedBoxes();

        if (servings != null && servings.getServings() != numberOfCheckedBoxes) {
            servings.setServings(numberOfCheckedBoxes);

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

    private Servings getServings() {
        return Servings.getByDateAndFood(day, food);
    }

    private void onServingsChanged() {
        new CalculateStreakTask(getContext()).execute(new StreakTaskInput(day, food));
    }
}
