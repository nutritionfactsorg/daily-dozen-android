package org.slavick.dailydozen.widgets;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Date;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

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

        Log.d(TAG, String.format("FoodServings: %s", food));

        tvName.setText(food.getName());

        CheckBox checkBox = createCheckBox();
        checkBox.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        final int checkboxWidth = checkBox.getMeasuredWidth();

        for (int i = 0; i < food.getRecommendedServings(); i++) {
            vgCheckboxes.addView(createCheckBox());
        }

        // The maximum number of servings for any food is 5. Here we set all FoodServings to have the same width of
        // checkboxes so they line up nicely.
        final ViewGroup.LayoutParams params = vgCheckboxes.getLayoutParams();
        params.width = checkboxWidth * 5;
        vgCheckboxes.setLayoutParams(params);
    }

    private CheckBox createCheckBox() {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setOnCheckedChangeListener(getOnCheckedChangeListener());
        return checkBox;
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {
        if (onCheckedChangeListener == null) {
            onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, String.format("%s: %s", food.getName(), getNumServings()));

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
    }

    private void handleServingUnchecked() {
        final Servings servings = Servings.createServingsIfDoesNotExist(date, food);
        servings.decreaseServings();

        if (servings.getServings() > 0) {
            servings.save();
        } else {
            servings.delete();
        }

        // TODO: 12/13/15
        // delete date if no other servings on that date
    }

    public int getNumServings() {
        int numEaten = 0;

        for (int i = 0; i < vgCheckboxes.getChildCount(); i++) {
            if (((CheckBox) vgCheckboxes.getChildAt(i)).isChecked()) {
                numEaten++;
            }
        }

        return numEaten;
    }
}
