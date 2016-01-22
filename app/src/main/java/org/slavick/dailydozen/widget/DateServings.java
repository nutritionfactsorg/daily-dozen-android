package org.slavick.dailydozen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.activity.ServingsHistoryActivity;
import org.slavick.dailydozen.model.Servings;

public class DateServings extends CardViewHeader {
    public DateServings(Context context) {
        super(context);
        init(context);
    }

    public DateServings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        setHeader(context.getString(R.string.servings));
    }

    public void setServings(final int servingsOnDate) {
        setSubHeader(String.format("%s out of 24   {fa-bar-chart 18dp @color/colorPrimary}", servingsOnDate));

        setSubHeaderOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();

                if (Servings.getTotalServingsCount() > 0) {
                    context.startActivity(new Intent(context, ServingsHistoryActivity.class));
                } else {
                    Common.showToast(context, R.string.no_servings_to_chart_toast);
                }
            }
        });
    }
}
