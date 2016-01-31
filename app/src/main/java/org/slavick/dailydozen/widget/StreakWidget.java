package org.slavick.dailydozen.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.joanzapata.iconify.widget.IconTextView;

import org.slavick.dailydozen.R;

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
            setVisibility(View.VISIBLE);

            setText(String.format("%s days", streak));

            if (streak > ONE_DAY && streak < ONE_WEEK) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                setBackgroundResource(R.drawable.rounded_rectangle_bronze);
            } else if (streak >= ONE_WEEK && streak < TWO_WEEKS) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                setBackgroundResource(R.drawable.rounded_rectangle_silver);
            } else if (streak >= TWO_WEEKS) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                setBackgroundResource(R.drawable.rounded_rectangle_gold);
            }
        } else {
            setVisibility(View.GONE);
        }
    }
}
