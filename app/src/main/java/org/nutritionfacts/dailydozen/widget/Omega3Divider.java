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

public class Omega3Divider extends LinearLayout {
    @BindView(R.id.omega3_divider_text)
    protected TextView tvText;

    public Omega3Divider(Context context) {
        super(context);
        init(context);
    }

    public Omega3Divider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Omega3Divider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.omega3_divider, this);
        ButterKnife.bind(this, view);

        tvText.setText(String.format("%s %s", getContext().getString(R.string.omega3_divider_text), getContext().getString(R.string.icon_info)));
    }

    @OnClick(R.id.omega3_divider_text)
    public void onOmega3DividerClicked() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.omega3_divider_text)
                .setMessage(R.string.dialog_omega3_explanation_text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}
