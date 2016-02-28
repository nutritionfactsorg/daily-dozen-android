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
        // Add some extra padding around the servings subheader so the user has a larger area to tap
        initPaddingAroundTextViews();

        setHeader(context.getString(R.string.servings));
    }

    private void initPaddingAroundTextViews() {
        final int dp8 = Common.convertDpToPx(getContext(), 8);
        final int dp16 = Common.convertDpToPx(getContext(), 16);

        tvHeader.setPadding(dp16, dp8, 0, dp8);
        tvSubHeader.setPadding(0, dp8, dp16, dp8);
    }

    public void setServings(final int servingsOnDate) {
        setSubHeader(String.format("%s out of 24   {fa-bar-chart 20dp @color/colorPrimary}", servingsOnDate));

        setSubHeaderOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();

                if (!Servings.isEmpty()) {
                    context.startActivity(new Intent(context, ServingsHistoryActivity.class));
                } else {
                    Common.showToast(context, R.string.no_servings_recorded);
                }
            }
        });
    }
}
