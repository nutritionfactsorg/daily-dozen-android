package org.slavick.dailydozen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.activity.CalendarActivity;
import org.slavick.dailydozen.model.Date;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodServings extends LinearLayout {
    private final static String TAG = FoodServings.class.getSimpleName();

    private Date date;
    private Food food;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    @Bind(R.id.food_checkboxes)
    protected ViewGroup vgCheckboxes;

    @Bind(R.id.food_name)
    protected TextView tvName;

    public FoodServings(Context context) {
        super(context);
    }

    public FoodServings(Context context, Date date, Food food) {
        super(context);

        this.date = date;
        this.food = food;

        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.food_item, this);
        ButterKnife.bind(this);

        initFoodName();
        initCheckboxes();
    }

    private void initFoodName() {
        tvName.setText(food.getName());

        tvName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();
                context.startActivity(new Intent(context, CalendarActivity.class));
            }
        });
    }

    private void initCheckboxes() {
        int numExistingServings = getNumExistingServings();

        final List<CheckBox> checkBoxes = new ArrayList<>();

        for (int i = 0; i < food.getRecommendedServings(); i++) {
            final boolean isChecked = numExistingServings > 0;

            checkBoxes.add(createCheckBox(isChecked));

            numExistingServings--;
        }

        // Add the checkboxes in reverse order because they were checked from left to right. Reversing the order
        // makes it so the checks appear right to left.
        Collections.reverse(checkBoxes);
        for (CheckBox checkBox : checkBoxes) {
            vgCheckboxes.addView(checkBox);
        }

        // The maximum number of servings for any food is 5. Here we set all FoodServings to have the same width of
        // checkboxes so they line up nicely.
        setWidthOfCheckBoxesToFiveWide();
    }

    private void setWidthOfCheckBoxesToFiveWide() {
        final CheckBox checkBox = new CheckBox(getContext());
        checkBox.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        final ViewGroup.LayoutParams params = vgCheckboxes.getLayoutParams();
        params.width = checkBox.getMeasuredWidth() * 5;
        vgCheckboxes.setLayoutParams(params);
    }

    private int getNumExistingServings() {
        final Servings servings = Servings.getByDateAndFood(date, food);
        return servings != null ? servings.getServings() : 0;
    }

    private CheckBox createCheckBox(final boolean isChecked) {
        final CheckBox checkBox = new CheckBox(getContext());

        // It is necessary to set the checked status before we set the onCheckedChangeListener
        checkBox.setChecked(isChecked);

        checkBox.setOnCheckedChangeListener(getOnCheckedChangeListener());

        return checkBox;
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
        if (onCheckedChangeListener == null) {
            onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        handleServingChecked();
                    } else {
                        handleServingUnchecked();
                    }
                }
            };
        }

        return onCheckedChangeListener;
    }

    private void handleServingChecked() {
        final Servings servings = Servings.createServingsIfDoesNotExist(date, food);
        servings.increaseServings();
        servings.save();

        Log.d(TAG, String.format("Increased Servings for %s", servings));
    }

    private void handleServingUnchecked() {
        final Servings servings = Servings.getByDateAndFood(date, food);
        if (servings != null) {
            servings.decreaseServings();

            if (servings.getServings() > 0) {
                servings.save();

                Log.d(TAG, String.format("Decreased Servings for %s", servings));
            } else {
                Log.d(TAG, String.format("Deleting %s", servings));
                servings.delete();
            }
        }
    }
}
