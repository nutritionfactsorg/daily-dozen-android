package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.event.TweakServingsChangedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TweakBoxes extends LinearLayout {
    private Day day;
    private Tweak tweak;

    @BindView(R.id.tweak_indent)
    protected View vIndent;
    @BindView(R.id.tweak_icon)
    protected ImageView ivIcon;
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

        final boolean foundTweakIcon = initTweakIcon();
        if (foundTweakIcon) {
            tvName.setText(this.tweak.getName());

            final TweakServings servings = getTweakServings();
            initCheckboxes(servings);
            initTweakStreak(servings);

            if (isDailyDoseTweak(tweak.getIdName())) {
                vIndent.setVisibility(VISIBLE);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean isDailyDoseTweak(final String tweakIdName) {
        switch (tweakIdName) {
            case "Daily Black Cumin":
            case "Daily Garlic":
            case "Daily Ginger":
            case "Daily NutriYeast":
            case "Daily Cumin":
            case "Daily Green Tea":
                return true;
            default:
                return false;
        }
    }

    private TweakServings getTweakServings() {
        return TweakServings.getByDateAndTweak(day, tweak);
    }

    private boolean initTweakIcon() {
        return Common.loadImage(getContext(), ivIcon, FoodInfo.getTweakIcon(tweak.getName()));
    }

    private void initTweakStreak(TweakServings servings) {
        final int streak = servings != null ? servings.getStreak() : 0;
        if (streak > 0) {
            tvStreak.setVisibility(VISIBLE);
            tvStreak.setStreak(streak);
        } else {
            tvStreak.setVisibility(GONE);
        }
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

    @Subscribe
    public void onEvent(TweakServingsChangedEvent event) {
        if (event.getTweakName().equals(tweak.getName())) {
            initTweakStreak(getTweakServings());
        }
    }
}
