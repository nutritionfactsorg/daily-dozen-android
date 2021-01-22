package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweakGroupHeader extends LinearLayout {
    @BindView(R.id.tweak_group_indent)
    protected View vIndent;
    @BindView(R.id.tweak_group_title)
    protected TextView tvTitle;

    public TweakGroupHeader(Context context) {
        super(context);
        init(context);
    }

    public TweakGroupHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TweakGroupHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.header_tweak_group, this);
        ButterKnife.bind(this, view);
    }

    public void setTweakGroup(final String tweakGroup) {
        switch (tweakGroup) {
            case Common.MEAL:
                tvTitle.setText(R.string.tweak_group_meal);
                break;
            case Common.DAILY:
                tvTitle.setText(R.string.tweak_group_daily);
                break;
            case Common.DAILY_DOSE:
                vIndent.setVisibility(VISIBLE);
                tvTitle.setText(R.string.tweak_group_dailydoses);
                break;
            case Common.NIGHTLY:
                tvTitle.setText(R.string.tweak_group_nightly);
                break;
        }
    }
}
