package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Weights;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LoadWeightsHistoryTask extends BaseTask<LoadHistoryCompleteEvent> {
    private static final int MONTHS_IN_YEAR = 12;
    private final ProgressListener progressListener;
    private final Context context;
    private final LoadHistoryTaskParams inputParams;

    public LoadWeightsHistoryTask(ProgressListener progressListener, Context context, LoadHistoryTaskParams inputParams) {
        this.progressListener = progressListener;
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
        progressListener.showProgressBar(R.string.task_loading_servings_history_title);
    }

    @Override
    public void setDataAfterLoading(LoadHistoryCompleteEvent event) {
        progressListener.hideProgressBar();

        Bus.loadServingsHistoryCompleteEvent(event);
    }

    // This method loads the last two months of servings into memory, but only shows the selected
    // month. This is because it needs to use the data from the month before to calculate the
    // starting moving average.
    private LoadHistoryCompleteEvent getChartDataInDays(final LoadHistoryTaskParams inputParams) {
        final List<Day> history = Day.getLastTwoMonths(inputParams.getSelectedYear(), inputParams.getSelectedMonth());

        final int numDaysOfWeights = history.size();

        final List<String> xLabels = new ArrayList<>(numDaysOfWeights);
        final List<BarEntry> barEntries = new ArrayList<>(numDaysOfWeights);
        final List<Entry> lineEntries = new ArrayList<>(numDaysOfWeights);

        float previousTrend = 0;
        float minWeight = Float.MAX_VALUE;
        float maxWeight = Float.MIN_VALUE;

        for (int i = 0; i < numDaysOfWeights; i++) {
            final Day day = history.get(i);

            Weights weightsOnDay = Weights.getWeightsOnDay(day);
            Float averageWeight;
            Float barEntryVal = null;

            if (weightsOnDay != null) {
                averageWeight = weightsOnDay.getAverageWeight();
                if (averageWeight != null) {
                    previousTrend = calculateTrend(previousTrend, averageWeight);
                    barEntryVal = averageWeight;

                    if (averageWeight > 0 && averageWeight < minWeight) {
                        minWeight = averageWeight;
                    }
                    if (averageWeight > maxWeight) {
                        maxWeight = averageWeight;
                    }
                }
            }

            // Only show the past days of servings in the selected month and year
            if (day.getYear() == inputParams.getSelectedYear() && day.getMonth() == inputParams.getSelectedMonth()) {
                final int xIndex = xLabels.size();

                xLabels.add(day.getDayOfWeek());

                if (barEntryVal != null) {
                    barEntries.add(new BarEntry(barEntryVal, xIndex));
                }

                final Entry lineEntry = new Entry(previousTrend, xIndex);
                // Here we set the optional data field on the Entry class. This gives the user the
                // ability to tap on a value in the LoadWeightsHistoryTask and be taken to that date
                lineEntry.setData(DateUtil.convertDayToDate(day));
                lineEntries.add(lineEntry);
            }

            progressListener.updateProgressBar(i + 1, numDaysOfWeights);
        }

        final LoadHistoryCompleteEvent weightHistory = createCompleteEvent(createLineAndBarData(xLabels, lineEntries, barEntries), TimeScale.DAYS);
        weightHistory.setMinVal(minWeight);
        weightHistory.setMaxVal(maxWeight);
        return weightHistory;
    }

    private LoadHistoryCompleteEvent getChartDataInMonths(final LoadHistoryTaskParams inputParams) {
        int i = 0;

        int year = inputParams.getSelectedYear();
        int monthOneBased = 1;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (monthOneBased <= MONTHS_IN_YEAR) {
            final float averageWeightInMonth = Weights.getAverageWeightInMonth(year, monthOneBased);

            Timber.d("getChartDataInMonths: year [%s], monthOneBased [%s], average weight [%s]",
                    year, monthOneBased, averageWeightInMonth);

            if (averageWeightInMonth > 0) {
                final int xIndex = xLabels.size();

                xLabels.add(DateUtil.getShortNameOfMonth(monthOneBased));

                lineEntries.add(new Entry(averageWeightInMonth, xIndex));
            }

            monthOneBased++;

            progressListener.updateProgressBar(i++, MONTHS_IN_YEAR);
        }

        return createCompleteEvent(createLineData(xLabels, lineEntries), TimeScale.MONTHS);
    }

    private LoadHistoryCompleteEvent getChartDataInYears() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        Timber.d("getChartDataInYears: firstYear [%s]", firstYear);

        final int currentYear = DateUtil.getCurrentYear();
        Timber.d("getChartDataInYears: currentYear [%s]", currentYear);

        final int numYears = currentYear - firstYear;
        int i = 0;

        int year = firstYear;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year <= currentYear) {
            final int xIndex = xLabels.size();

            xLabels.add(String.valueOf(year));

            final float averageWeightInYear = Weights.getAverageWeightInYear(year);

            Timber.d("getChartDataInYears: year [%s], average weight [%s]",
                    year, averageWeightInYear);

            lineEntries.add(new Entry(averageWeightInYear, xIndex));

            year++;

            progressListener.updateProgressBar(i++, numYears);
        }

        return createCompleteEvent(createLineData(xLabels, lineEntries), TimeScale.YEARS);
    }

    private LoadHistoryCompleteEvent createCompleteEvent(final CombinedData combinedData,
                                                         @TimeScale.Interface final int timeScale) {
        return new LoadHistoryCompleteEvent(combinedData, timeScale);
    }

    private CombinedData createLineAndBarData(List<String> xLabels, List<Entry> lineEntries, List<BarEntry> barEntries) {
        final CombinedData combinedData = createLineData(xLabels, lineEntries);
        combinedData.setData(getBarData(xLabels, barEntries));
        return combinedData;
    }

    private CombinedData createLineData(final List<String> xLabels, final List<Entry> lineEntries) {
        final CombinedData combinedData = new CombinedData(xLabels);
        combinedData.setData(getLineData(xLabels, lineEntries));
        return combinedData;
    }

    // Calculates an exponentially smoothed moving average with 10% smoothing
    private float calculateTrend(float previousTrend, float currentValue) {
        if (previousTrend == 0) {
            return currentValue;
        } else {
            // Tn = Tn-1 + 0.1 * (Vn - Tn-1)
            // Tn is Trend for day n (today)
            // Tn-1 is Trend for day n-1 (yesterday)
            // Vn is Value for day n (total servings today)
            return previousTrend + 0.1f * (currentValue - previousTrend);
        }
    }

    private BarData getBarData(List<String> xVals, List<BarEntry> barEntries) {
        final BarDataSet dataSet = new BarDataSet(barEntries, context.getString(R.string.average_weight_on_day));

        dataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(context, android.R.color.white));
        dataSet.setValueTextSize(14);

        // We just want the value as an integer
        dataSet.setValueFormatter(new BarChartValueFormatter());

        return new BarData(xVals, dataSet);
    }

    private LineData getLineData(List<String> xVals, List<Entry> lineEntries) {
        final LineDataSet dataSet = new LineDataSet(lineEntries, context.getString(R.string.moving_average));

        final int color = ContextCompat.getColor(context, R.color.brown);

        dataSet.setColor(color);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(color);
        dataSet.setFillColor(color);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12);
        dataSet.setValueTextColor(color);

        // Format the value labels to two decimal places
        dataSet.setValueFormatter(new LineChartValueFormatter());

        return new LineData(xVals, dataSet);
    }

    private static class BarChartValueFormatter implements ValueFormatter {
        private final DecimalFormat decimalFormat;

        BarChartValueFormatter() {
            decimalFormat = new DecimalFormat("#.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return decimalFormat.format(value);
        }
    }

    private static class LineChartValueFormatter implements ValueFormatter {
        private final DecimalFormat decimalFormat;

        LineChartValueFormatter() {
            decimalFormat = new DecimalFormat("#.00");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return decimalFormat.format(value);
        }
    }
}
