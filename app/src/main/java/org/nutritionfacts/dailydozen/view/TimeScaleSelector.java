package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.TimeScaleSelectorBinding;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class TimeScaleSelector extends LinearLayout implements AdapterView.OnItemSelectedListener {
    private TimeScaleSelectorBinding binding;

    public TimeScaleSelector(Context context) {
        this(context, null);
    }

    public TimeScaleSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeScaleSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TimeScale.Interface
    public int getSelectedTimeScale() {
        switch (binding.timeScaleSpinner.getSelectedItemPosition()) {
            case TimeScale.MONTHS:
                return TimeScale.MONTHS;
            case TimeScale.YEARS:
                return TimeScale.YEARS;
            default:
            case TimeScale.DAYS:
                return TimeScale.DAYS;
        }
    }

    private void init(final Context context) {
        binding = TimeScaleSelectorBinding.inflate(LayoutInflater.from(context), this, true);
        initTimeScaleSpinner();
    }

    private void initTimeScaleSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.servings_time_scale_choices,
                android.R.layout.simple_expandable_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.timeScaleSpinner.setOnItemSelectedListener(this);
        binding.timeScaleSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Bus.timeScaleSelected(binding.timeScaleSpinner.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
