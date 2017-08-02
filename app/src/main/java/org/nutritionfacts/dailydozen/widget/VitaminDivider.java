package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VitaminDivider extends LinearLayout {
    @BindView(R.id.vitamin_divider_text)
    protected TextView tvText;

    public VitaminDivider(Context context) {
        super(context);
        init(context);
    }

    public VitaminDivider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VitaminDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.vitamin_divider, this);
        ButterKnife.bind(this, view);

        tvText.setText(String.format("%s %s", getContext().getString(R.string.vitamin_divider_text), getContext().getString(R.string.icon_info)));
    }

    @OnClick(R.id.vitamin_divider_text)
    public void onVitaminDividerClicked() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.vitamin_divider_text)
                .setMessage(R.string.dialog_vitamin_explanation_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
