package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.TweakBoxesBinding;
import org.nutritionfacts.dailydozen.event.TweakServingsChangedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;

public class TweakBoxes extends LinearLayout {
    private TweakBoxesBinding binding;
    private Day day;
    private Tweak tweak;

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
        binding = TweakBoxesBinding.inflate(LayoutInflater.from(context), this, true);
        onTweakNameClicked();
        onTweakHistoryClicked();
    }

    public boolean setDateAndTweak(final Day day, final Tweak tweak) {
        this.day = day;
        this.tweak = tweak;

        final boolean foundTweakIcon = initTweakIcon();
        if (foundTweakIcon) {
            initTweakName();

            final TweakServings servings = getTweakServings();
            initCheckboxes(servings);
            initTweakStreak(servings);

            if (isDailyDoseTweak(tweak.getIdName())) {
                binding.tweakIndent.setVisibility(VISIBLE);
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
        return Common.loadImage(getContext(), binding.tweakIcon, FoodInfo.getTweakIcon(tweak.getName()));
    }

    private void initTweakName() {
        binding.tweakName.setText(String.format("%s %s", tweak.getName(), getContext().getString(R.string.icon_info)));
    }

    private void initTweakStreak(TweakServings servings) {
        final int streak = servings != null ? servings.getStreak() : 0;
        if (streak > 0) {
            binding.tweakStreak.setVisibility(VISIBLE);
            binding.tweakStreak.setStreak(streak);
        } else {
            binding.tweakStreak.setVisibility(GONE);
        }
    }

    public void onTweakNameClicked() {
        binding.tweakName.setOnClickListener(v -> Common.openTweakInfo(getContext(), tweak));
        binding.tweakIcon.setOnClickListener(v -> Common.openTweakInfo(getContext(), tweak));
    }

    public void onTweakHistoryClicked() {
        binding.tweakHistory.setOnClickListener(v -> Common.openTweakHistory(getContext(), tweak));
        binding.tweakStreak.setOnClickListener(v -> Common.openTweakHistory(getContext(), tweak));
    }

    private void initCheckboxes(TweakServings servings) {
        binding.tweakCheckboxes.setDay(day);
        binding.tweakCheckboxes.setRDA(tweak);
        binding.tweakCheckboxes.setServings(servings);
    }

    @Subscribe
    public void onEvent(TweakServingsChangedEvent event) {
        if (event.getTweakName().equals(tweak.getName())) {
            initTweakStreak(getTweakServings());
        }
    }
}
