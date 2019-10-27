package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.joanzapata.iconify.widget.IconTextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Weights;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import timber.log.Timber;

public class DateWeights extends LinearLayout {
    @BindView(R.id.header)
    protected TextView tvHeader;
    @BindView(R.id.eye)
    protected TextView tvEye;
    @BindView(R.id.morning_weight)
    protected TextView tvMorningWeight;
    @BindView(R.id.morning_weight_hidden_icon)
    protected IconTextView tvMorningWeightHiddenIcon;
    @BindView(R.id.evening_weight)
    protected TextView tvEveningWeight;
    @BindView(R.id.evening_weight_hidden_icon)
    protected IconTextView tvEveningWeightHiddenIcon;

    private boolean showWeights = true;
    private Day day;

    public DateWeights(Context context) {
        super(context);
        init(context);
    }

    public DateWeights(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        final View view = inflate(context, R.layout.date_weights, this);
        ButterKnife.bind(this, view);

        tvHeader.setText("Weight");
    }

    public void setDay(final Day day) {
        this.day = day;

        // TODO: 2019-10-22 load weights
    }

    @OnClick(R.id.eye)
    public void onEyeClicked() {
        showWeights = !showWeights;

        if (showWeights) {
            setWeightsVisible();
        } else {
            setWeightsInvisible();
        }
    }

    private void setWeightsVisible() {
        tvEye.setText(R.string.date_weights_eye_open);
        tvMorningWeight.setVisibility(VISIBLE);
        tvMorningWeightHiddenIcon.setVisibility(GONE);
        tvEveningWeight.setVisibility(VISIBLE);
        tvEveningWeightHiddenIcon.setVisibility(GONE);
    }

    private void setWeightsInvisible() {
        tvEye.setText(R.string.date_weights_eye_closed);
        tvMorningWeight.setVisibility(GONE);
        tvMorningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(tvMorningWeight.getText())) {
            tvMorningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            tvMorningWeightHiddenIcon.setText(R.string.checked);
        }

        tvEveningWeight.setVisibility(GONE);
        tvEveningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(tvEveningWeight.getText())) {
            tvEveningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            tvEveningWeightHiddenIcon.setText(R.string.checked);
        }
    }

    @OnTextChanged({R.id.morning_weight, R.id.evening_weight})
    public void onWeightChanged() {
        day = Day.createDayIfDoesNotExist(day);

        final Weights weights = Weights.createWeightsIfDoesNotExist(day);
        if (weights != null) {
            float morningWeight = Float.parseFloat(tvMorningWeight.getText().toString());
            float eveningWeight = Float.parseFloat(tvEveningWeight.getText().toString());

            weights.setMorningWeight(morningWeight);
            weights.setEveningWeight(eveningWeight);

            weights.save();
            Timber.d("Saving morning weight [%s] and evening weight [%s]", morningWeight, eveningWeight);
        }
    }

    @OnEditorAction({R.id.morning_weight,R.id.evening_weight})
    public boolean onMorningWeightEditorAction(EditText et, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            et.clearFocus();
        }
        return false;
    }

    public void setMorningWeight(final float weight) {
        if (weight > 0) {
            tvMorningWeight.setText(String.valueOf(weight));
        }
    }

    public void setEveningWeight(final float weight) {
        if (weight > 0) {
            tvEveningWeight.setText(String.valueOf(weight));
        }
    }
}
