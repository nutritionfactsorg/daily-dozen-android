package org.slavick.dailydozen.task;

import android.content.Context;
import android.support.v4.content.ContextCompat;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoadServingsHistoryTask extends TaskWithContext<Integer, Integer, CombinedData> {
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

        final int maxDaysToLoad = params[0];

        final List<Day> history = maxDaysToLoad == -1 ? Day.getAllDays() : Day.getHistory(maxDaysToLoad);

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
