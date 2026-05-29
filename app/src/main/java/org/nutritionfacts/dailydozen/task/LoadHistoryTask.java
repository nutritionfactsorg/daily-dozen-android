package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.model.enums.HistoryType;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;
import org.nutritionfacts.dailydozen.util.DateUtil;
import org.nutritionfacts.dailydozen.util.HistoryChartHelper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LoadHistoryTask extends BaseTask<LoadHistoryCompleteEvent> {
    private static final int MONTHS_IN_YEAR = 12;
    private final Context context;
    private final LoadHistoryTaskParams inputParams;

    public LoadHistoryTask(Context context, LoadHistoryTaskParams inputParams) {
        this.context = context;
        this.inputParams = inputParams;
    }

    @Override
    public LoadHistoryCompleteEvent call() {
        switch (inputParams.getTimeScale()) {
            default:
            case TimeScale.DAYS:
                return getChartDataInDays(inputParams);
            case TimeScale.MONTHS:
                return getChartDataInMonths(inputParams);
            case TimeScale.YEARS:
                return getChartDataInYears();
        }
    }

    @Override
    public void setUiForLoading() {
    }

    @Override
    public void setDataAfterLoading(LoadHistoryCompleteEvent event) {
        Bus.loadHistoryCompleteEvent(event);
    }

    // This method loads the last two months of history into memory, but only shows the selected
    // month. This is because it needs to use the data from the month before to calculate the
    // starting moving average.
    private LoadHistoryCompleteEvent getChartDataInDays(final LoadHistoryTaskParams inputParams) {
        final List<Day> history = Day.getLastTwoMonths(inputParams.getSelectedYear(), inputParams.getSelectedMonth());

        final int numDaysOfHistory = history.size();

        final List<String> xLabels = new ArrayList<>(numDaysOfHistory);
        final List<BarEntry> barEntries = new ArrayList<>(numDaysOfHistory);
        final List<Entry> lineEntries = new ArrayList<>(numDaysOfHistory);

        float previousTrend = 0;

        for (int i = 0; i < numDaysOfHistory; i++) {
            final Day day = history.get(i);

            final int totalOnDate = getTotalOnDate(day);

            previousTrend = HistoryChartHelper.calculateTrend(previousTrend, totalOnDate);

            // Only show the past days of history in the selected month and year
            if (day.getYear() == inputParams.getSelectedYear() && day.getMonth() == inputParams.getSelectedMonth()) {
                final int xIndex = xLabels.size();

                xLabels.add(day.getDayOfWeek());

                barEntries.add(new BarEntry(totalOnDate, xIndex));

                final Entry lineEntry = new Entry(previousTrend, xIndex);
                // Here we set the optional data field on the Entry class. This gives the user the
                // ability to tap on a value in the ServingsHistoryActivity and be taken to that date
                lineEntry.setData(DateUtil.convertDayToDate(day));
                lineEntries.add(lineEntry);
            }
        }

        return completeEvent(lineAndBarData(xLabels, lineEntries, barEntries));
    }

    private LoadHistoryCompleteEvent getChartDataInMonths(final LoadHistoryTaskParams inputParams) {
        int year = inputParams.getSelectedYear();
        int monthOneBased = 1;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (monthOneBased <= MONTHS_IN_YEAR) {
            final float averageForMonth = getAverageForMonth(year, monthOneBased);

            Timber.d("getChartDataInMonths: year [%s], monthOneBased [%s], average [%s]",
                    year, monthOneBased, averageForMonth);

            if (averageForMonth > 0) {
                final int xIndex = xLabels.size();

                xLabels.add(DateUtil.getShortNameOfMonth(monthOneBased));

                lineEntries.add(new Entry(averageForMonth, xIndex));
            }

            monthOneBased++;
        }

        return completeEvent(HistoryChartHelper.lineData(context, xLabels, lineEntries));
    }

    private LoadHistoryCompleteEvent getChartDataInYears() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        Timber.d("getChartDataInYears: firstYear [%s]", firstYear);

        final int currentYear = DateUtil.getCurrentYear();
        Timber.d("getChartDataInYears: currentYear [%s]", currentYear);

        int year = firstYear;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year <= currentYear) {
            final int xIndex = xLabels.size();

            xLabels.add(String.valueOf(year));

            final float averageForYear = getAverageForYear(year);

            Timber.d("getChartDataInYears: year [%s], average [%s]",
                    year, averageForYear);

            lineEntries.add(new Entry(averageForYear, xIndex));

            year++;
        }

        return completeEvent(HistoryChartHelper.lineData(context, xLabels, lineEntries));
    }

    private LoadHistoryCompleteEvent completeEvent(final CombinedData combinedData) {
        return HistoryChartHelper.completeEvent(combinedData, inputParams.getTimeScale());
    }

    private CombinedData lineAndBarData(
            List<String> xLabels,
            List<Entry> lineEntries,
            List<BarEntry> barEntries) {
        return HistoryChartHelper.lineAndBarData(
                context,
                xLabels,
                lineEntries,
                barEntries,
                getBarDataLabelForHistoryType(),
                HistoryChartHelper.BAR_VALUE_FORMAT_INTEGER);
    }

    private int getTotalOnDate(final Day day) {
        switch (inputParams.getHistoryType()) {
            case HistoryType.FoodServings:
                return DDServings.getTotalServingsOnDate(day);
            case HistoryType.Tweaks:
                return TweakServings.getTotalTweakServingsOnDate(day);
            default:
                return 0;
        }
    }

    private float getAverageForMonth(final int year, final int monthOneBased) {
        switch (inputParams.getHistoryType()) {
            case HistoryType.FoodServings:
                return DDServings.getAverageTotalServingsInMonth(year, monthOneBased);
            case HistoryType.Tweaks:
                return TweakServings.getAverageTotalTweakServingsInMonth(year, monthOneBased);
            default:
                return 0;
        }
    }

    private float getAverageForYear(final int year) {
        switch (inputParams.getHistoryType()) {
            case HistoryType.FoodServings:
                return DDServings.getAverageTotalServingsInYear(year);
            case HistoryType.Tweaks:
                return TweakServings.getAverageTotalTweakServingsInYear(year);
            default:
                return 0;
        }
    }

    private String getBarDataLabelForHistoryType() {
        switch (inputParams.getHistoryType()) {
            case HistoryType.FoodServings:
                return context.getString(R.string.servings);
            case HistoryType.Tweaks:
                return context.getString(R.string.tweaks);
            default:
                return "";
        }
    }
}
