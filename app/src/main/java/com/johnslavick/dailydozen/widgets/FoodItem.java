package com.johnslavick.dailydozen.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnslavick.dailydozen.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodItem extends LinearLayout {
    @Bind(R.id.food_checkboxes)
    protected ViewGroup checkboxes;

    @Bind(R.id.food_name)
    protected TextView name;

    public FoodItem(Context context) {
        this(context, null);
    }

    public FoodItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        inflate(context, R.layout.food_item, this);
        ButterKnife.bind(this);

        applyLayoutAttributes(context, attrs);
    }

    private void applyLayoutAttributes(final Context context, final AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FoodItem);
            if (array != null) {
                final int numCheckboxes = array.getInt(R.styleable.FoodItem_numCheckboxes, 0);
                final String foodName = array.getString(R.styleable.FoodItem_foodName);

                createCheckboxes(context, numCheckboxes);
                name.setText(foodName);

                array.recycle();
            }
        }
    }

    private void createCheckboxes(final Context context, final int numCheckboxes) {
        for (int i = 0; i < numCheckboxes; i++) {
            checkboxes.addView(new CheckBox(context));
        }
    }
}
