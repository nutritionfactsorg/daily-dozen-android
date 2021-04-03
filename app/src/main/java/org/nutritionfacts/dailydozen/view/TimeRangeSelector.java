package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.TimeRangeSelectorBinding;
import org.nutritionfacts.dailydozen.event.TimeScaleSelectedEvent;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.util.DateUtil;

public class TimeRangeSelector extends LinearLayout {
    private TimeRangeSelectorBinding binding;

    private int minYear;
    private int maxYear;
    private int minMonthOfMinYear;
    private int maxMonthOfMaxYear;

    private int selectedYear;
    private int selectedMonth;

    public void setStartAndEnd(final int startYear, final int startMonth,
                               final int endYear, final int endMonth) {
        this.minYear = startYear;
        this.maxYear = endYear;
        this.minMonthOfMinYear = startMonth;
        this.maxMonthOfMaxYear = endMonth;

        setSelectedMonth(maxMonthOfMaxYear);
        setSelectedYear(maxYear);
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int year) {
        this.selectedYear = year;
        updateYearDisplay();
        updateMonthDisplay();
    }

    private void increaseYear() {
        if (selectedYear + 1 <= maxYear) {
            setSelectedYear(selectedYear + 1);

            if (selectedYear == maxYear && this.selectedMonth > maxMonthOfMaxYear) {
                setSelectedMonth(maxMonthOfMaxYear);
            }
        }
    }

    private void decreaseYear() {
        if (selectedYear - 1 >= minYear) {
            setSelectedYear(selectedYear - 1);

            if (selectedYear == minYear && this.selectedMonth < minMonthOfMinYear) {
                setSelectedMonth(minMonthOfMinYear);
            }
        }
    }

    private void updateYearDisplay() {
        binding.timeRangeSelectorSelectedYear.setText(String.valueOf(this.selectedYear));

        // Disable previous or next buttons if we are at the min or max year for entries
        binding.timeRangeSelectorPreviousYear.setVisibility(this.selectedYear == minYear ? INVISIBLE : VISIBLE);
        binding.timeRangeSelectorNextYear.setVisibility(this.selectedYear == maxYear ? INVISIBLE : VISIBLE);
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int month) {
        selectedMonth = month;

        if (selectedMonth == 0) {
            selectedMonth = 12;
            decreaseYear();
        } else if (selectedMonth == 13) {
            selectedMonth = 1;
            increaseYear();
        }

        updateMonthDisplay();
    }

    private void increaseMonth() {
        if (selectedYear < maxYear || selectedMonth + 1 <= maxMonthOfMaxYear) {
            setSelectedMonth(selectedMonth + 1);
        }
    }

    private void decreaseMonth() {
        if (selectedYear > minYear || selectedMonth - 1 >= minMonthOfMinYear) {
            setSelectedMonth(selectedMonth - 1);
        }
    }

    private void updateMonthDisplay() {
        binding.timeRangeSelectorSelectedMonth.setText(DateUtil.getShortNameOfMonth(this.selectedMonth));

        // Disable previous or next buttons if we are at the min or max month for entries
        binding.timeRangeSelectorPreviousMonth.setVisibility(this.selectedYear == minYear && this.selectedMonth == minMonthOfMinYear ? INVISIBLE : VISIBLE);
        binding.timeRangeSelectorNextMonth.setVisibility(this.selectedYear == maxYear && this.selectedMonth == maxMonthOfMaxYear ? INVISIBLE : VISIBLE);

        // Here we show/hide the go to earliest and go latest buttons. The go to earliest button is only shown if
        // there is at least one month or one year in the past from the currently selected time.
        if (binding.timeRangeSelectorMonthContainer.getVisibility() == VISIBLE) {
            binding.timeRangeSelectorEarliest.setVisibility(binding.timeRangeSelectorPreviousMonth.getVisibility());
            binding.timeRangeSelectorLatest.setVisibility(binding.timeRangeSelectorNextMonth.getVisibility());
        } else if (binding.timeRangeSelectorYearContainer.getVisibility() == VISIBLE) {
            binding.timeRangeSelectorEarliest.setVisibility(binding.timeRangeSelectorPreviousYear.getVisibility());
            binding.timeRangeSelectorLatest.setVisibility(binding.timeRangeSelectorNextYear.getVisibility());
        } else {
            binding.timeRangeSelectorEarliest.setVisibility(GONE);
            binding.timeRangeSelectorLatest.setVisibility(GONE);
        }
    }

    public TimeRangeSelector(Context context) {
        this(context, null);
    }

    public TimeRangeSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeRangeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        binding = TimeRangeSelectorBinding.inflate(LayoutInflater.from(context), this, true);

        onPreviousYearClicked();
        onNextYearClicked();
        onPreviousMonthClicked();
        onNextMonthClicked();
        onEarliestClicked();
        onLatestClicked();
    }

    public void onPreviousYearClicked() {
        binding.timeRangeSelectorPreviousYear.setOnClickListener(v -> {
            decreaseYear();
            postTimeRangeSelectedEvent();
        });
    }

    public void onNextYearClicked() {
        binding.timeRangeSelectorNextYear.setOnClickListener(v -> {
            increaseYear();
            postTimeRangeSelectedEvent();
        });
    }

    public void onPreviousMonthClicked() {
        binding.timeRangeSelectorPreviousMonth.setOnClickListener(v -> {
            decreaseMonth();
            postTimeRangeSelectedEvent();
        });
    }

    public void onNextMonthClicked() {
        binding.timeRangeSelectorNextMonth.setOnClickListener(v -> {
            increaseMonth();
            postTimeRangeSelectedEvent();
        });
    }

    private void postTimeRangeSelectedEvent() {
        Bus.timeRangeSelectedEvent();
    }

    @Subscribe
    public void onEvent(TimeScaleSelectedEvent event) {
        binding.timeRangeSelectorYearContainer.setVisibility(VISIBLE);
        binding.timeRangeSelectorMonthContainer.setVisibility(VISIBLE);

        switch (event.getSelectedTimeScale()) {
            case TimeScale.DAYS:
            default:
                break;
            case TimeScale.MONTHS:
                binding.timeRangeSelectorMonthContainer.setVisibility(GONE);
                break;
            case TimeScale.YEARS:
                binding.timeRangeSelectorYearContainer.setVisibility(GONE);
                binding.timeRangeSelectorMonthContainer.setVisibility(GONE);
                break;
        }

        updateYearDisplay();
        updateMonthDisplay();
    }

    public void onEarliestClicked() {
        binding.timeRangeSelectorEarliest.setOnClickListener(v -> {
            setSelectedYear(minYear);
            setSelectedMonth(minMonthOfMinYear);
            postTimeRangeSelectedEvent();
        });
    }

    public void onLatestClicked() {
        binding.timeRangeSelectorLatest.setOnClickListener(v -> {
            setSelectedYear(maxYear);
            setSelectedMonth(maxMonthOfMaxYear);
            postTimeRangeSelectedEvent();
        });
    }
}
