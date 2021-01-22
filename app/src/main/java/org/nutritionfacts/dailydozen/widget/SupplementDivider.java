package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.nutritionfacts.dailydozen.R;

import butterknife.ButterKnife;

public class SupplementDivider extends LinearLayout {
    public SupplementDivider(Context context) {
        super(context);
        init(context);
    }

    public SupplementDivider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SupplementDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.supplement_divider, this);
        ButterKnife.bind(this, view);
    }
}
