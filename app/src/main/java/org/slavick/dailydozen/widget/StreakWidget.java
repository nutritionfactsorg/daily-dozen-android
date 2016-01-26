package org.slavick.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.joanzapata.iconify.widget.IconTextView;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.ServingsStreak;

import java.util.Date;

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

    public void setDateAndFood(final Date date, final Food food) {
        final ServingsStreak servingsStreak = ServingsStreak.getStreakOnDateForFood(date, food);
        if (servingsStreak != null && servingsStreak.getStreak() > 1) {
            final int streak = servingsStreak.getStreak();

            setVisibility(View.VISIBLE);

            setText(String.format("{fa-trophy} %s days", streak));

            if (streak > 1 && streak < 5) {
                setBackgroundResource(R.drawable.rounded_rectangle_bronze);
            } else if (streak >= 5 && streak < 7) {
                setBackgroundResource(R.drawable.rounded_rectangle_silver);
            } else if (streak >= 7) {
                setBackgroundResource(R.drawable.rounded_rectangle_gold);
            }
        } else {
            setVisibility(View.GONE);
        }
    }
}
