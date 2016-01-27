package org.slavick.dailydozen.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.joanzapata.iconify.widget.IconTextView;

import org.slavick.dailydozen.R;

public class StreakWidget extends IconTextView {
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
        if (streak > 1) {
            setVisibility(View.VISIBLE);

            setText(String.format("%s days", streak));

            if (streak > 1 && streak < 5) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                setBackgroundResource(R.drawable.rounded_rectangle_bronze);
            } else if (streak >= 5 && streak < 7) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                setBackgroundResource(R.drawable.rounded_rectangle_silver);
            } else if (streak >= 7) {
                setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                setBackgroundResource(R.drawable.rounded_rectangle_gold);
            }
        } else {
            setVisibility(View.GONE);
        }
    }
}
