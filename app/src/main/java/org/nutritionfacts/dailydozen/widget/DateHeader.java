package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.DDServings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class DateHeader extends LinearLayout {
    @BindView(R.id.header)
    protected TextView tvHeader;
    @BindView(R.id.star)
    protected TextView tvStar;
    @BindView(R.id.num_servings)
    protected TextView tvNumServings;
    @BindView(R.id.max)
    protected TextView tvMax;

    public DateHeader(Context context) {
        super(context);
        init(context, null);
    }

    public DateHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attrs) {
        final View view = inflate(context, R.layout.header_date, this);
        ButterKnife.bind(this, view);

        setTitle(context.getString(R.string.servings));

        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            handleCustomAttrs(context, attrs);
        }
    }

    private void setTitle(final String title) {
        tvHeader.setText(title);
    }

    private void setMax(final String max) {
        tvMax.setText(max);
    }

    private void handleCustomAttrs(final Context context, final AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DateHeader);
        if (array != null) {
            setTitle(array.getString(R.styleable.DateHeader_title));
            setMax(String.valueOf(array.getInt(R.styleable.DateHeader_max, 24)));
            array.recycle();
        }
    }

    public void setServings(final int servingsOnDate) {
        tvStar.setVisibility(servingsOnDate == Common.MAX_SERVINGS ? VISIBLE : GONE);

        tvNumServings.setText(Integer.toString(servingsOnDate));
    }

    @OnClick(R.id.sub_header)
    public void onSubHeaderClicked() {
        final Context context = getContext();

        // TODO (slavick) this should open tweaks history when in 21 tweaks mode
        if (!DDServings.isEmpty()) {
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
