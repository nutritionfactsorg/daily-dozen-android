package org.nutritionfacts.dailydozen.task;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoadServingsHistoryTask extends TaskWithContext<Integer, Integer, CombinedData> {
    private static final String TAG = LoadServingsHistoryTask.class.getSimpleName();

    public LoadServingsHistoryTask(Context context) {
        super(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(R.string.task_loading_servings_history_title);
        progress.show();
    }

    @Override
    protected CombinedData doInBackground(Integer... params) {
        if (Servings.isEmpty()) {
            return null;
        }

        switch (params[0]) {
            default:
            case TimeScale.DAYS:
                return getChartDataInDays();
            case TimeScale.MONTHS:
                return getChartDataInMonths();
            case TimeScale.YEARS:
                return getChartDataInYears();
        }
    }

    private CombinedData getChartDataInDays() {
        final List<Day> history = Day.getLastSixtyDays();

        final int numDaysOfServings = history.size();

        final List<String> xLabels = new ArrayList<>(numDaysOfServings);
        final List<BarEntry> barEntries = new ArrayList<>(numDaysOfServings);
        final List<Entry> lineEntries = new ArrayList<>(numDaysOfServings);

        float previousTrend = 0;

        for (int i = 0; i < numDaysOfServings; i++) {
            if (isCancelled()) {
                break;
            }

            final Day day = history.get(i);

            final int totalServingsOnDate = Servings.getTotalServingsOnDate(day);

            previousTrend = calculateTrend(previousTrend, totalServingsOnDate);

            // Only show the past 30 days of servings when showing daily servings history
            if (numDaysOfServings < 30 || numDaysOfServings - i <= 30) {
                final int xIndex = xLabels.size();

                xLabels.add(day.getDayOfWeek());
                barEntries.add(new BarEntry(totalServingsOnDate, xIndex));
                lineEntries.add(new Entry(previousTrend, xIndex));
            }

            publishProgress(i + 1, numDaysOfServings);
        }

        return createLineAndBarData(xLabels, lineEntries, barEntries);
    }

    private CombinedData getChartDataInMonths() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        final int firstMonthOneBased = firstDay.getMonth();
        Log.d(TAG, String.format("getChartDataInMonths: firstYear [%s], firstMonthOneBased [%s]",
                firstYear, firstMonthOneBased));

        final int currentYear = DateUtil.getCurrentYear();
        final int currentMonthOneBased = DateUtil.getCurrentMonthOneBased();
        Log.d(TAG, String.format("getChartDataInMonths: currentYear [%s], currentMonthOneBased [%s]",
                currentYear, currentMonthOneBased));

        final Calendar cal = DateUtil.getCalendarForYearAndMonth(firstYear, firstMonthOneBased - 1);

        final int numMonths = DateUtil.monthsSince(cal);
        int i = 0;

        int year = firstYear;
        int monthOneBased = firstMonthOneBased;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year < currentYear || (year == currentYear && monthOneBased <= currentMonthOneBased)) {
            if (isCancelled()) {
                break;
            }

            final int xIndex = xLabels.size();

            xLabels.add(String.format("%s/%s", monthOneBased, year));

            final float averageTotalServingsInMonth = Servings.getAverageTotalServingsInMonth(year, monthOneBased);

            Log.d(TAG, String.format("getChartDataInMonths: year [%s], monthOneBased [%s], average [%s]",
                    year, monthOneBased, averageTotalServingsInMonth));

            lineEntries.add(new Entry(averageTotalServingsInMonth, xIndex));

            DateUtil.addOneMonth(cal);
            year = DateUtil.getYear(cal);
            monthOneBased = DateUtil.getMonthOneBased(cal);

            publishProgress(i++, numMonths);
        }

        return createLineData(xLabels, lineEntries);
    }

    private CombinedData getChartDataInYears() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        Log.d(TAG, String.format("getChartDataInYears: firstYear [%s]", firstYear));

        final int currentYear = DateUtil.getCurrentYear();
        Log.d(TAG, String.format("getChartDataInYears: currentYear [%s]", currentYear));

        final int numYears = currentYear - firstYear;
        int i = 0;

        int year = firstYear;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year <= currentYear) {
            if (isCancelled()) {
                break;
            }

            final int xIndex = xLabels.size();

            xLabels.add(String.valueOf(year));

            final float averageTotalServingsInYear = Servings.getAverageTotalServingsInYear(year);

            Log.d(TAG, String.format("getChartDataInYears: year [%s], average [%s]",
                    year, averageTotalServingsInYear));

            lineEntries.add(new Entry(averageTotalServingsInYear, xIndex));

            year++;

            publishProgress(i++, numYears);
        }

        return createLineData(xLabels, lineEntries);
    }

    private CombinedData createLineAndBarData(List<String> xLabels, List<Entry> lineEntries, List<BarEntry> barEntries) {
        final CombinedData combinedData = createLineData(xLabels, lineEntries);
        if (combinedData != null) {
            combinedData.setData(getBarData(xLabels, barEntries));
        }
        return combinedData;
    }

    private CombinedData createLineData(final List<String> xLabels, final List<Entry> lineEntries) {
        if (isCancelled()) {
            return null;
        } else {
            final CombinedData combinedData = new CombinedData(xLabels);
            combinedData.setData(getLineData(xLabels, lineEntries));
            return combinedData;
        }
    }

    // Calculates an exponentially smoothed moving average with 10% smoothing
    private float calculateTrend(float previousTrend, int currentValue) {
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
        final BarDataSet dataSet = new BarDataSet(barEntries, getContext().getString(R.string.servings));

        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        dataSet.setValueTextSize(14);

        // We just want the value as an integer
        dataSet.setValueFormatter(new BarChartValueFormatter());

        return new BarData(xVals, dataSet);
    }

    private LineData getLineData(List<String> xVals, List<Entry> lineEntries) {
        final LineDataSet dataSet = new LineDataSet(lineEntries, getContext().getString(R.string.moving_average));

        final int color = ContextCompat.getColor(getContext(), R.color.colorAccent);

        dataSet.setColor(color);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(color);
        dataSet.setFillColor(color);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12);
        dataSet.setValueTextColor(color);

        return new LineData(xVals, dataSet);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (values.length == 2) {
            progress.setProgress(values[0]);
            progress.setMax(values[1]);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Bus.loadServingsHistoryCompleteEvent(null);
    }

    @Override
    protected void onPostExecute(CombinedData chartData) {
        super.onPostExecute(chartData);
        Bus.loadServingsHistoryCompleteEvent(chartData);
    }

    private class BarChartValueFormatter implements com.github.mikephil.charting.formatter.ValueFormatter {
        private DecimalFormat decimalFormat;

        public BarChartValueFormatter() {
            decimalFormat = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return decimalFormat.format(value);
        }
    }
}
