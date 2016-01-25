package org.slavick.dailydozen.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.activity.FoodHistoryActivity;
import org.slavick.dailydozen.activity.FoodInfoActivity;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.model.ServingsStreak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FoodServings extends RecyclerView.ViewHolder {
    private final static String TAG = FoodServings.class.getSimpleName();

    private Date date;
    private Food food;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private TextView tvName;
    private TextView tvStreak;
    private ViewGroup vgCheckboxes;
    private IconTextView ivFoodHistory;

    private ClickListener listener;

    public FoodServings(View itemView) {
        super(itemView);

        tvName = (TextView) itemView.findViewById(R.id.food_name);
        tvStreak = (TextView) itemView.findViewById(R.id.food_streak);
        vgCheckboxes = (ViewGroup) itemView.findViewById(R.id.food_checkboxes);
        ivFoodHistory = (IconTextView) itemView.findViewById(R.id.food_history);
    }

    private Context getContext() {
        return itemView.getContext();
    }

    public void setDateAndFood(final Date date, final Food food) {
        this.date = date;
        this.food = food;

        initFoodName();
        initCheckboxes();
        initFoodInfo();
    }

    public void setStreak(Date date, Food food) {
        // TODO: 1/25/16 load streak from database

        final int streak = ServingsStreak.getStreakOnDateForFood(date, food);
        if (streak > 1) {
            tvStreak.setText(String.format("%s days", streak));
        }
    }

    private void initFoodName() {
        tvName.setText(String.format("%s %s", food.getName(), getContext().getString(R.string.icon_info)));

        tvName.setOnClickListener(getOnFoodNameClickListener());
    }

    private View.OnClickListener getOnFoodNameClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(createFoodIntent(FoodInfoActivity.class, food));
            }
        };
    }

    private void initFoodInfo() {
        ivFoodHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(createFoodIntent(FoodHistoryActivity.class, food));
            }
        });
    }

    private Intent createFoodIntent(final Class<? extends AppCompatActivity> klass, final Food food) {
        final Intent intent = new Intent(getContext(), klass);
        intent.putExtra(Args.FOOD_ID, food.getId());
        return intent;
    }

    private void initCheckboxes() {
        int numExistingServings = getNumExistingServings();

        final List<CheckBox> checkBoxes = new ArrayList<>();

        for (int i = 0; i < food.getRecommendedServings(); i++) {
            final boolean isChecked = numExistingServings > 0;

            checkBoxes.add(createCheckBox(isChecked));

            numExistingServings--;
        }

        vgCheckboxes.removeAllViews();

        // Add the checkboxes in reverse order because they were checked from left to right. Reversing the order
        // makes it so the checks appear right to left.
        Collections.reverse(checkBoxes);
        for (CheckBox checkBox : checkBoxes) {
            vgCheckboxes.addView(checkBox);
        }
    }

    private int getNumExistingServings() {
        final Servings servings = Servings.getByDateAndFood(date, food);
        return servings != null ? servings.getServings() : 0;
    }

    private CheckBox createCheckBox(final boolean isChecked) {
        final CheckBox checkBox = new CheckBox(itemView.getContext());

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
        if (servings != null) {
            servings.increaseServings();
            servings.save();

            informListener();

            // TODO: 1/25/16 recalculate streak

            Log.d(TAG, String.format("Increased Servings for %s", servings));
        }
    }

    private void handleServingUnchecked() {
        final Servings servings = Servings.getByDateAndFood(date, food);
        if (servings != null) {
            servings.decreaseServings();

            // TODO: 1/25/16 recalculate streak

            if (servings.getServings() > 0) {
                servings.save();

                Log.d(TAG, String.format("Decreased Servings for %s", servings));
            } else {
                Log.d(TAG, String.format("Deleting %s", servings));
                servings.delete();
            }

            informListener();
        }
    }

    private void informListener() {
        if (listener != null) {
            listener.onServingsChanged();
        }
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        void onServingsChanged();
    }
}
