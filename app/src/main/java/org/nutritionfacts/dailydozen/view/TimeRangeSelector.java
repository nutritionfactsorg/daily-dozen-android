package org.nutritionfacts.dailydozen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.TimeScaleSelectedEvent;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.util.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeRangeSelector extends LinearLayout {
    @BindView(R.id.time_range_selector_earliest)
    protected View btnEarliest;
    @BindView(R.id.time_range_selector_latest)
    protected View btnLatest;
    @BindView(R.id.time_range_selector_year_container)
    protected ViewGroup vgYearContainer;
    @BindView(R.id.time_range_selector_previous_year)
    protected TextView btnPreviousYear;
    @BindView(R.id.time_range_selector_selected_year)
    protected TextView tvSelectedYear;
    @BindView(R.id.time_range_selector_next_year)
    protected TextView btnNextYear;
    @BindView(R.id.time_range_selector_month_container)
    protected ViewGroup vgMonthContainer;
    @BindView(R.id.time_range_selector_previous_month)
    protected TextView btnPreviousMonth;
    @BindView(R.id.time_range_selector_selected_month)
    protected TextView tvSelectedMonth;
    @BindView(R.id.time_range_selector_next_month)
    protected TextView btnNextMonth;

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
        tvSelectedYear.setText(String.valueOf(this.selectedYear));

        // Disable previous or next buttons if we are at the min or max year for entries
        btnPreviousYear.setVisibility(this.selectedYear == minYear ? INVISIBLE : VISIBLE);
        btnNextYear.setVisibility(this.selectedYear == maxYear ? INVISIBLE : VISIBLE);
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
        tvSelectedMonth.setText(DateUtil.getShortNameOfMonth(this.selectedMonth));

        // Disable previous or next buttons if we are at the min or max month for entries
        btnPreviousMonth.setVisibility(this.selectedYear == minYear && this.selectedMonth == minMonthOfMinYear ? INVISIBLE : VISIBLE);
        btnNextMonth.setVisibility(this.selectedYear == maxYear && this.selectedMonth == maxMonthOfMaxYear ? INVISIBLE : VISIBLE);

        // Here we show/hide the go to earliest and go latest buttons. The go to earliest button is only shown if
        // there is at least one month or one year in the past from the currently selected time.
        if (vgMonthContainer.getVisibility() == VISIBLE) {
            btnEarliest.setVisibility(btnPreviousMonth.getVisibility());
            btnLatest.setVisibility(btnNextMonth.getVisibility());
        } else if (vgYearContainer.getVisibility() == VISIBLE) {
            btnEarliest.setVisibility(btnPreviousYear.getVisibility());
            btnLatest.setVisibility(btnNextYear.getVisibility());
        } else {
            btnEarliest.setVisibility(GONE);
            btnLatest.setVisibility(GONE);
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
        inflate(context, R.layout.time_range_selector, this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.time_range_selector_previous_year)
    public void onPreviousYearClicked() {
        decreaseYear();
        postTimeRangeSelectedEvent();
    }

    @OnClick(R.id.time_range_selector_next_year)
    public void onNextYearClicked() {
        increaseYear();
        postTimeRangeSelectedEvent();
    }

    @OnClick(R.id.time_range_selector_previous_month)
    public void onPreviousMonthClicked() {
        decreaseMonth();
        postTimeRangeSelectedEvent();
    }

    @OnClick(R.id.time_range_selector_next_month)
    public void onNextMonthClicked() {
        increaseMonth();
        postTimeRangeSelectedEvent();
    }

    private void postTimeRangeSelectedEvent() {
        Bus.timeRangeSelectedEvent();
    }

    @Subscribe
    public void onEvent(TimeScaleSelectedEvent event) {
        vgYearContainer.setVisibility(VISIBLE);
        vgMonthContainer.setVisibility(VISIBLE);

        switch (event.getSelectedTimeScale()) {
            case TimeScale.DAYS:
            default:
                break;
            case TimeScale.MONTHS:
                vgMonthContainer.setVisibility(GONE);
                break;
            case TimeScale.YEARS:
                vgYearContainer.setVisibility(GONE);
                vgMonthContainer.setVisibility(GONE);
                break;
        }

        updateYearDisplay();
        updateMonthDisplay();
    }

    @OnClick(R.id.time_range_selector_earliest)
    public void onEarliestClicked() {
        setSelectedYear(minYear);
        setSelectedMonth(minMonthOfMinYear);
        postTimeRangeSelectedEvent();
    }

    @OnClick(R.id.time_range_selector_latest)
    public void onLatestClicked() {
        setSelectedYear(maxYear);
        setSelectedMonth(maxMonthOfMaxYear);
        postTimeRangeSelectedEvent();
    }
}
