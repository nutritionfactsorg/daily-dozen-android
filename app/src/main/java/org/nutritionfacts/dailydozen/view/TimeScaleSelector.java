package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeScaleSelector extends LinearLayout implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.time_scale_spinner)
    protected Spinner timeScaleSpinner;

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
        switch (timeScaleSpinner.getSelectedItemPosition()) {
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
        inflate(context, R.layout.time_scale_selector, this);
        ButterKnife.bind(this);
        initTimeScaleSpinner();
    }

    private void initTimeScaleSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.servings_time_scale_choices,
                android.R.layout.simple_expandable_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeScaleSpinner.setOnItemSelectedListener(this);
        timeScaleSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Bus.timeScaleSelected(timeScaleSpinner.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
