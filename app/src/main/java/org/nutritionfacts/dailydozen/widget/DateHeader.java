package org.nutritionfacts.dailydozen.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.HeaderDateBinding;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.TweakServings;

public class DateHeader extends LinearLayout {
    private HeaderDateBinding binding;

    private int max;

    public DateHeader(Context context) {
        super(context);
        init(context, null);
    }

    public DateHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        binding = HeaderDateBinding.inflate(LayoutInflater.from(context), this, true);

        onSubHeaderClicked();
        onStarLongClicked();

        setTitle(context.getString(R.string.servings));

        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            handleCustomAttrs(context, attrs);
        }
    }

    private void setTitle(final String title) {
        binding.header.setText(title);
    }

    private void setMax(final String max) {
        binding.max.setText(max);
        this.max = Integer.parseInt(max);
    }

    private void handleCustomAttrs(final Context context, final AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DateHeader);
        if (array != null) {
            setTitle(array.getString(R.styleable.DateHeader_title));
            setMax(String.valueOf(array.getInt(R.styleable.DateHeader_max, 24)));
            array.recycle();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setServings(final int servingsOnDate) {
        // Only show the star for the Daily Dozen checklist
        if (inDailyDozenMode()) {
            binding.star.setVisibility(servingsOnDate == Common.MAX_SERVINGS ? VISIBLE : GONE);
        }

        binding.numServings.setText(Integer.toString(servingsOnDate));
    }

    private boolean inDailyDozenMode() {
        return max == Common.MAX_SERVINGS;
    }

    public void onSubHeaderClicked() {
        binding.subHeader.setOnClickListener(v -> {
            final Context context = getContext();

            if (inDailyDozenMode()) {
                if (!DDServings.isEmpty()) {
                    Common.openServingsHistory(context);
                } else {
                    Common.showToast(context, R.string.no_servings_recorded);
                }
            } else {
                if (!TweakServings.isEmpty()) {
                    Common.openTweakServingsHistory(context);
                } else {
                    Common.showToast(context, R.string.no_servings_recorded);
                }
            }
        });
    }

    public void onStarLongClicked() {
        binding.star.setOnLongClickListener(v -> {
            Bus.showExplodingStarAnimation();
            return true;
        });
    }
}
