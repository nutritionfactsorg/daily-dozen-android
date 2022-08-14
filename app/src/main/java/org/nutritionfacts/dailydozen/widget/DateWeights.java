package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.DateWeightsBinding;
import org.nutritionfacts.dailydozen.event.WeightVisibilityChangedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Weights;

import timber.log.Timber;

public class DateWeights extends LinearLayout {
    private DateWeightsBinding binding;

    private boolean initialized = false;
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
        binding = DateWeightsBinding.inflate(LayoutInflater.from(context), this, true);

        binding.header.setText(R.string.weight);
        
        onEyeClicked();
        onWeightHistoryClicked();
        onWeightEditorAction();
        onWeightChanged();
    }

    public void setDay(final Day day) {
        this.day = day;

        final Weights weightsOnDay = Weights.getWeightsOnDay(day);
        if (weightsOnDay != null) {
            if (weightsOnDay.getMorningWeight() > 0) {
                binding.morningWeight.setText(String.valueOf(weightsOnDay.getMorningWeight()));
            }
            if (weightsOnDay.getEveningWeight() > 0) {
                binding.eveningWeight.setText(String.valueOf(weightsOnDay.getEveningWeight()));
            }
        }

        updateWeights();

        initialized = true;
    }

    @Subscribe
    public void onEvent(WeightVisibilityChangedEvent event) {
        updateWeights();
    }

    public void onEyeClicked() {
        binding.eye.setOnClickListener(v -> {
            Prefs.getInstance(getContext()).toggleWeightVisibility();
            Bus.weightVisibilityChanged();
        });
    }

    public void onWeightHistoryClicked() {
        binding.weightHistory.setOnClickListener(v -> {
            final Context context = getContext();
            if (!Weights.isEmpty()) {
                Common.openWeightHistory(context);
            } else {
                Common.showToast(context, R.string.no_weights_recorded);
            }
        });
    }

    private void setWeightsVisible() {
        binding.eye.setText(R.string.date_weights_eye_open);

        binding.morningWeight.setVisibility(VISIBLE);
        binding.morningWeightHiddenIcon.setVisibility(GONE);

        binding.eveningWeight.setVisibility(VISIBLE);
        binding.eveningWeightHiddenIcon.setVisibility(GONE);
    }

    private void setWeightsInvisible() {
        binding.eye.setText(R.string.date_weights_eye_closed);

        binding.morningWeight.setVisibility(GONE);
        binding.morningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(binding.morningWeight.getText())) {
            binding.morningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            binding.morningWeightHiddenIcon.setText(R.string.checked);
        }

        binding.eveningWeight.setVisibility(GONE);
        binding.eveningWeightHiddenIcon.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(binding.eveningWeight.getText())) {
            binding.eveningWeightHiddenIcon.setText(R.string.unchecked);
        } else {
            binding.eveningWeightHiddenIcon.setText(R.string.checked);
        }
    }

    public void onWeightChanged() {
        TextWatcher weightTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!initialized) {
                    return;
                }

                try {
                    float morningWeight = 0;
                    float eveningWeight = 0;

                    String morningWeightStr = binding.morningWeight.getText().toString();
                    if (!TextUtils.isEmpty(morningWeightStr)) {
                        morningWeight = Float.parseFloat(morningWeightStr);
                    }

                    String eveningWeightStr = binding.eveningWeight.getText().toString();
                    if (!TextUtils.isEmpty(eveningWeightStr)) {
                        eveningWeight = Float.parseFloat(eveningWeightStr);
                    }

                    if (morningWeight > 0 || eveningWeight > 0) {
                        day = Day.createDayIfDoesNotExist(day);
                        boolean weightSaved = Weights.createWeightsIfDoesNotExist(day, morningWeight, eveningWeight);
                        if (weightSaved) {
                            Timber.d("Saving morning weight [%s] and evening weight [%s]", morningWeight, eveningWeight);
                        }
                    }
                } catch (NumberFormatException e) {
                    Timber.e(e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.morningWeight.addTextChangedListener(weightTextWatcher);
        binding.eveningWeight.addTextChangedListener(weightTextWatcher);
    }

    public void onWeightEditorAction() {
        binding.morningWeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
            }
            return false;
        });

        binding.eveningWeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus();
            }
            return false;
        });
    }

    private void updateWeights() {
        if (Prefs.getInstance(getContext()).getWeightVisible()) {
            setWeightsVisible();
        } else {
            setWeightsInvisible();
        }
    }
}
