package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.RDA;
import org.nutritionfacts.dailydozen.Servings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.task.CalculateStreakTask;
import org.nutritionfacts.dailydozen.task.StreakTaskInput;
import org.nutritionfacts.dailydozen.view.ServingCheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RDACheckBoxes extends LinearLayout {
    @BindView(R.id.food_check_boxes_container)
    protected ViewGroup vgContainer;

    private List<ServingCheckBox> checkBoxes;

    private RDA rda;
    private Day day;

    public RDACheckBoxes(Context context) {
        this(context, null);
    }

    public RDACheckBoxes(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RDACheckBoxes(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setRDA(RDA rda) {
        this.rda = rda;
    }

    public void setServings(final Servings servings) {
        checkBoxes = new ArrayList<>();
        createCheckBox(
                checkBoxes,
                servings != null ? servings.getServings() : 0,
                rda.getRecommendedAmount());

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

                if (rda instanceof Food) {
                    if (isChecked) {
                        handleServingChecked();
                    } else {
                        handleServingUnchecked();
                    }
                } else if (rda instanceof Tweak) {
                    if (isChecked) {
                        handleTweakChecked();
                    } else {
                        handleTweakUnchecked();
                    }
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

        final DDServings servings = DDServings.createServingsIfDoesNotExist(day, (Food)rda);
        final Integer numberOfCheckedBoxes = getNumberOfCheckedBoxes();

        if (servings != null && servings.getServings() != numberOfCheckedBoxes) {
            servings.setServings(numberOfCheckedBoxes);

            servings.save();
            onServingsChanged();
            Timber.d("Increased Servings for %s", servings);
        }
    }

    private void handleServingUnchecked() {
        final DDServings servings = DDServings.getByDateAndFood(day, (Food) rda);
        final Integer numberOfCheckedBoxes = getNumberOfCheckedBoxes();

        if (servings != null && servings.getServings() != numberOfCheckedBoxes) {
            servings.setServings(numberOfCheckedBoxes);

            if (servings.getServings() > 0) {
                servings.save();
                Timber.d("Decreased Servings for %s", servings);
            } else {
                Timber.d("Deleting %s", servings);
                servings.delete();
            }

            onServingsChanged();
        }
    }

    private void handleTweakChecked() {
        // TODO (slavick)
        Common.showToast(getContext(), "not implemented yet");
    }

    private void handleTweakUnchecked() {
        // TODO (slavick)
        Common.showToast(getContext(), "not implemented yet");
    }

    private void onServingsChanged() {
        new CalculateStreakTask(getContext()).execute(new StreakTaskInput(day, (Food)rda));
    }
}
