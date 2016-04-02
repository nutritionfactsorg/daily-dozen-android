package org.slavick.dailydozen.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.activity.ServingsHistoryActivity;
import org.slavick.dailydozen.controller.Bus;
import org.slavick.dailydozen.model.Servings;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class DateServings extends LinearLayout {
    @Bind(R.id.header)
    protected TextView tvHeader;
    @Bind(R.id.star)
    protected TextView tvStar;
    @Bind(R.id.sub_header)
    protected TextView tvSubHeader;

    public DateServings(Context context) {
        super(context);
        init(context);
    }

    public DateServings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        final View view = inflate(context, R.layout.date_servings, this);
        ButterKnife.bind(this, view);

        // Add some extra padding around the servings sub header so the user has a larger area to tap
        initPaddingAroundTextViews();

        setHeader(context.getString(R.string.servings));
    }

    public void setHeader(final String text) {
        tvHeader.setText(text);
    }

    public void setSubHeader(final String text) {
        tvSubHeader.setText(text);
    }

    private void initPaddingAroundTextViews() {
        final int dp8 = Common.convertDpToPx(getContext(), 8);
        final int dp16 = Common.convertDpToPx(getContext(), 16);

        tvHeader.setPadding(dp16, dp8, 0, dp8);
        tvSubHeader.setPadding(0, dp8, dp16, dp8);
    }

    public void setServings(final int servingsOnDate) {
        tvStar.setVisibility(servingsOnDate == 24 ? VISIBLE : GONE);

        setSubHeader(String.format("%s out of 24   {fa-bar-chart 20dp @color/gray_dark}", servingsOnDate));
    }

    @OnClick(R.id.sub_header)
    public void onSubHeaderClicked() {
        final Context context = getContext();

        if (!Servings.isEmpty()) {
            context.startActivity(new Intent(context, ServingsHistoryActivity.class));
        } else {
            Common.showToast(context, R.string.no_servings_recorded);
        }
    }

    @OnLongClick(R.id.star)
    public boolean onStarLongClicked() {
        Bus.showExplodingStarAnimation();
        return true;
    }
}
