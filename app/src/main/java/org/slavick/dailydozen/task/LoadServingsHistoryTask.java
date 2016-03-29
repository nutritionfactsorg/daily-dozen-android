package org.slavick.dailydozen.task;

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

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.model.enums.TimeScale;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LoadServingsHistoryTask extends TaskWithContext<Integer, Integer, CombinedData> {
    private static final String TAG = LoadServingsHistoryTask.class.getSimpleName();

    private final static double AVERAGE_MILLIS_PER_YEAR = 365.24 * 24 * 60 * 60 * 1000;
    private final static double AVERAGE_MILLIS_PER_MONTH = AVERAGE_MILLIS_PER_YEAR / 12;

    private final Listener listener;

    public interface Listener {
        void onLoadServings(CombinedData chartData);
        void onLoadServingsCancelled();
    }

    public LoadServingsHistoryTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
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
        final List<Day> history = Day.getAllDays();

        final int numDaysOfServings = history.size();

        final List<String> xLabels = new ArrayList<>(numDaysOfServings);
        final List<BarEntry> barEntries = new ArrayList<>(numDaysOfServings);
        final List<Entry> lineEntries = new ArrayList<>(numDaysOfServings);

        float previousTrend = 0;

        for (int i = 0; i < numDaysOfServings; i++) {
            if (isCancelled()) {
                break;
            }

            final int xIndex = xLabels.size();

            final Day day = history.get(i);
            xLabels.add(day.getDayOfWeek());

            final int totalServingsOnDate = Servings.getTotalServingsOnDate(day);

            barEntries.add(new BarEntry(totalServingsOnDate, xIndex));

            previousTrend = calculateTrend(previousTrend, totalServingsOnDate);
            lineEntries.add(new Entry(previousTrend, xIndex));

            publishProgress(i + 1, numDaysOfServings);
        }

        if (isCancelled()) {
            return null;
        } else {
            final CombinedData combinedData = new CombinedData(xLabels);
            combinedData.setData(getBarData(xLabels, barEntries));
            combinedData.setData(getLineData(xLabels, lineEntries));
            return combinedData;
        }
    }

    private CombinedData getChartDataInMonths() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        final int firstMonth = firstDay.getMonth();
        Log.d(TAG, String.format("getChartDataInMonths: firstYear [%s], firstMonth [%s]", firstYear, firstMonth));

        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH) + 1;
        Log.d(TAG, String.format("getChartDataInMonths: currentYear [%s], currentMonth [%s]", currentYear, currentMonth));

        cal.set(Calendar.YEAR, firstDay.getYear());
        cal.set(Calendar.MONTH, firstDay.getMonth() - 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final int numMonths = monthsBetween(cal, Calendar.getInstance(Locale.getDefault()));
        int i = 0;

        int year = firstYear;
        int month = firstMonth;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year < currentYear || (year == currentYear && month <= currentMonth)) {
            final int xIndex = xLabels.size();

            xLabels.add(String.format("%s/%s", month, year));

            final float averageTotalServingsInMonth = Servings.getAverageTotalServingsInMonth(cal);

            lineEntries.add(new Entry(averageTotalServingsInMonth, xIndex));

            publishProgress(i++, numMonths);

            cal.add(Calendar.MONTH, 1);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;

            Log.d(TAG, String.format("getChartDataInMonths: year [%s], month [%s], average [%s]", year, month, averageTotalServingsInMonth));
        }

        if (isCancelled()) {
            return null;
        } else {
            final CombinedData combinedData = new CombinedData(xLabels);
            combinedData.setData(getLineData(xLabels, lineEntries));
            return combinedData;
        }
    }

    private CombinedData getChartDataInYears() {
        final Day firstDay = Day.getFirstDay();
        final int firstYear = firstDay.getYear();
        Log.d(TAG, String.format("getChartDataInMonths: firstYear [%s]", firstYear));

        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        final int currentYear = cal.get(Calendar.YEAR);
        Log.d(TAG, String.format("getChartDataInMonths: currentYear [%s]", currentYear));

        cal.set(Calendar.YEAR, firstDay.getYear());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final int numYears = yearsBetween(cal, Calendar.getInstance(Locale.getDefault()));
        int i = 0;

        int year = firstYear;

        final List<String> xLabels = new ArrayList<>();
        final List<Entry> lineEntries = new ArrayList<>();

        while (year <= currentYear) {
            final int xIndex = xLabels.size();

            xLabels.add(String.valueOf(year));

            final float averageTotalServingsInYear = Servings.getAverageTotalServingsInYear(cal);

            lineEntries.add(new Entry(averageTotalServingsInYear, xIndex));

            publishProgress(i++, numYears);

            cal.add(Calendar.YEAR, 1);
            year = cal.get(Calendar.YEAR);

            Log.d(TAG, String.format("getChartDataInMonths: year [%s], average [%s]", year, averageTotalServingsInYear));
        }

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
        final BarDataSet dataSet = new BarDataSet(barEntries, "Servings");

        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        dataSet.setValueTextSize(14);

        // We just want the value as an integer
        dataSet.setValueFormatter(new BarChartValueFormatter());

        return new BarData(xVals, dataSet);
    }

    private LineData getLineData(List<String> xVals, List<Entry> lineEntries) {
        final LineDataSet dataSet = new LineDataSet(lineEntries, "Moving Average");

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
        listener.onLoadServingsCancelled();
    }

    @Override
    protected void onPostExecute(CombinedData chartData) {
        super.onPostExecute(chartData);
        listener.onLoadServings(chartData);
    }

    // These methods are meant to calculate a rough approximation of the number of months and the number of years between
    // a start date and an end date. The output is meant only for showing progress when loading data from the database.
    private static int monthsBetween(Calendar start, Calendar end) {
        return timeBetween(start, end, AVERAGE_MILLIS_PER_MONTH);
    }

    private static int yearsBetween(Calendar start, Calendar end) {
        return timeBetween(start, end, AVERAGE_MILLIS_PER_YEAR);
    }

    private static int timeBetween(Calendar start, Calendar end, double millis) {
        return (int) ((end.getTime().getTime() - start.getTime().getTime()) / millis);
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
