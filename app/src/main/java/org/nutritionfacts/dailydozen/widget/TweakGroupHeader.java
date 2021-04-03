package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.HeaderTweakGroupBinding;

public class TweakGroupHeader extends LinearLayout {
    private HeaderTweakGroupBinding binding;

    public TweakGroupHeader(Context context) {
        super(context);
        init(context);
    }

    public TweakGroupHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TweakGroupHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        binding = HeaderTweakGroupBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setTweakGroup(final String tweakGroup) {
        switch (tweakGroup) {
            case Common.MEAL:
                binding.tweakGroupTitle.setText(R.string.tweak_group_meal);
                break;
            case Common.DAILY:
                binding.tweakGroupTitle.setText(R.string.tweak_group_daily);
                break;
            case Common.DAILY_DOSE:
                binding.tweakGroupIndent.setVisibility(VISIBLE);
                binding.tweakGroupTitle.setText(R.string.tweak_group_dailydoses);
                break;
            case Common.NIGHTLY:
                binding.tweakGroupTitle.setText(R.string.tweak_group_nightly);
                break;
        }
    }
}
