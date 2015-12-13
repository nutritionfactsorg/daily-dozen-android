package org.slavick.dailydozen.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.slavick.dailydozen.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodItem extends LinearLayout {
    private final static String TAG = FoodItem.class.getSimpleName();

    private String name;
    private int quantity;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

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

        CheckBox checkBox = createCheckBox();
        checkBox.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        final int checkboxWidth = checkBox.getMeasuredWidth();

        for (int i = 0; i < quantity; i++) {
            vgCheckboxes.addView(createCheckBox());
        }

        // The maximum number of servings for any food is 5. Here we set all FoodItems to have the same width of
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
                    Log.d(TAG, String.format("%s: %s", name, getNumServings()));

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
        Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void handleServingUnchecked() {
        Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
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
