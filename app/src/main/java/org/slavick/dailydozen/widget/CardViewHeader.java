package org.slavick.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slavick.dailydozen.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CardViewHeader extends LinearLayout {
    @Bind(R.id.header)
    protected TextView tvHeader;

    @Bind(R.id.subheader)
    protected TextView tvSubHeader;

    public CardViewHeader(Context context) {
        super(context);
        init(context);
    }

    public CardViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.card_view_header, this);
        ButterKnife.bind(this);
    }

    public void setHeader(final String text) {
        tvHeader.setText(text);
    }

    public void setSubHeader(final String text) {
        tvSubHeader.setText(text);
    }
}
