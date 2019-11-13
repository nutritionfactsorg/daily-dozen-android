package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweakBoxes extends LinearLayout {
    private Day day;
    private Tweak tweak;

    @BindView(R.id.tweak_name)
    protected TextView tvName;
    @BindView(R.id.tweak_streak)
    protected StreakWidget tvStreak;
    @BindView(R.id.tweak_checkboxes)
    protected RDACheckBoxes rdaCheckBoxes;

    public TweakBoxes(Context context) {
        super(context);
        init(context);
    }

    public TweakBoxes(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TweakBoxes(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.tweak_boxes, this);
        ButterKnife.bind(this, view);
    }

    public boolean setDateAndTweak(final Day day, final Tweak tweak) {
        this.day = day;
        this.tweak = tweak;

        initTweakName();

        final TweakServings servings = TweakServings.getByDateAndTweak(this.day, this.tweak);
        initCheckboxes(servings);
        // TODO (slavick)
//        initTweakStreak(servings);

        return true;
    }

    private void initTweakName() {
        tvName.setText(String.format("%s %s", tweak.getName(), getContext().getString(R.string.icon_info)));
    }

//    private void initTweakStreak(DDServings servings) {
//        final int streak = servings != null ? servings.getStreak() : 0;
//        if (streak > 0) {
//            tvStreak.setVisibility(VISIBLE);
//            tvStreak.setStreak(streak);
//        } else {
//            tvStreak.setVisibility(GONE);
//        }
//    }

    @OnClick(R.id.tweak_name)
    public void onTweakNameClicked() {
        Common.openTweakInfo(getContext(), tweak);
    }

    @OnClick({R.id.tweak_history, R.id.tweak_streak})
    public void onTweakHistoryClicked() {
        Common.openTweakHistory(getContext(), tweak);
    }

    private void initCheckboxes(TweakServings servings) {
        rdaCheckBoxes.setDay(day);
        rdaCheckBoxes.setRDA(tweak);
        rdaCheckBoxes.setServings(servings);
    }

//    @Subscribe
//    public void onEvent(FoodServingsChangedEvent event) {
//        if (event.getFoodName().equals(food.getName())) {
//            initTweakStreak(getServings());
//        }
//    }
}
