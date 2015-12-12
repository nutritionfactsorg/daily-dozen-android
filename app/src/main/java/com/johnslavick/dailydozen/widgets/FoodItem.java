package com.johnslavick.dailydozen.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnslavick.dailydozen.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodItem extends LinearLayout {
    private final static String TAG = FoodItem.class.getSimpleName();

    private String name;
    private int quantity;

    @Bind(R.id.food_checkboxes)
    protected ViewGroup vgCheckboxes;

    @Bind(R.id.food_name)
    protected TextView tvName;

    public FoodItem(Context context) {
        this(context, null);
    }

    public FoodItem(Context context, String name, int quantity) {
        super(context);

        this.name = name;
        this.quantity = quantity;

        init(context);
    }

    public FoodItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FoodItem);
            if (array != null) {
                this.quantity = array.getInt(R.styleable.FoodItem_numCheckboxes, 0);
                this.name = array.getString(R.styleable.FoodItem_foodName);
                array.recycle();
            }
        }

        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.food_item, this);
        ButterKnife.bind(this);

        Log.d(TAG, String.format("FoodItem: name [%s] quantity [%s]", name, quantity));

        tvName.setText(name);

        final CheckBox checkBox = new CheckBox(context);
        checkBox.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        final int checkboxWidth = checkBox.getMeasuredWidth();

        for (int i = 0; i < quantity; i++) {
            vgCheckboxes.addView(new CheckBox(context));
        }

        // The maximum number of servings for any food is 5. Here we set all FoodItems to have the same width of
        // checkboxes so they line up nicely.
        final ViewGroup.LayoutParams params = vgCheckboxes.getLayoutParams();
        params.width = checkboxWidth * 5;
        vgCheckboxes.setLayoutParams(params);
    }
}
