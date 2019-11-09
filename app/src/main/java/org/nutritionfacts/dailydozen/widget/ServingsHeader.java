package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.Servings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ServingsHeader extends LinearLayout {
    @BindView(R.id.header)
    protected TextView tvHeader;
    @BindView(R.id.star)
    protected TextView tvStar;
    @BindView(R.id.num_servings)
    protected TextView tvNumServings;

    public ServingsHeader(Context context) {
        super(context);
        init(context);
    }

    public ServingsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        final View view = inflate(context, R.layout.header_servings, this);
        ButterKnife.bind(this, view);

        tvHeader.setText(context.getString(R.string.servings));
    }

    public void setServings(final int servingsOnDate) {
        tvStar.setVisibility(servingsOnDate == Common.MAX_SERVINGS ? VISIBLE : GONE);

        tvNumServings.setText(Integer.toString(servingsOnDate));
    }

    @OnClick(R.id.sub_header)
    public void onSubHeaderClicked() {
        final Context context = getContext();

        if (!Servings.isEmpty()) {
            Common.openServingsHistory(context);
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
