package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.joanzapata.iconify.widget.IconTextView;

import org.nutritionfacts.dailydozen.R;

public class StreakWidget extends IconTextView {
    private static final int ONE_DAY = 1;
    private static final int ONE_WEEK = 7;
    private static final int TWO_WEEKS = 14;

    public StreakWidget(Context context) {
        super(context);
    }

    public StreakWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StreakWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setStreak(int streak) {
        if (streak > ONE_DAY) {
            setForceDarkAllowed(false);
            setVisibility(View.VISIBLE);

            setText(getContext().getString(R.string.format_num_days, streak));

            if (streak < ONE_WEEK) {
                setBackgroundAndTextColor(R.drawable.rounded_rectangle_bronze, android.R.color.white);
            } else if (streak < TWO_WEEKS) {
                setBackgroundAndTextColor(R.drawable.rounded_rectangle_silver, android.R.color.black);
            } else {
                setBackgroundAndTextColor(R.drawable.rounded_rectangle_gold, android.R.color.black);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    private void setBackgroundAndTextColor(@DrawableRes int drawableId, @ColorRes int colorId) {
        setBackgroundResource(drawableId);
        setTextColor(ContextCompat.getColor(getContext(), colorId));
    }
}
