package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ServingsHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        initChart();
    }

    private void initChart() {
        final BarChart chart = (BarChart) findViewById(R.id.daily_servings_chart);

        chart.setData(getChartData());

        // Only show 1 week at a time and start the chart with the latest day in view
        chart.setVisibleXRange(7, 7);
        chart.moveViewToX(chart.getXChartMax());

        chart.setDescription("");

        // Prevents the value for each bar from drawing over the labels at the top
        chart.setDrawValueAboveBar(false);

        // Even though we hide the left axis, we must set its max value so that full servings days reach the top
        chart.getAxisLeft().setAxisMaxValue(24);
        chart.getAxisLeft().setEnabled(false);

        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

        // Disable all zooming and interacting with the chart
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
    }

    private BarData getChartData() {
        final List<Day> allDays = Day.getAllDays();

        final List<String> xVals = new ArrayList<>(allDays.size());
        final List<BarEntry> yVals = new ArrayList<>(allDays.size());

        for (Day day : allDays) {
            xVals.add(day.getDayOfWeek());

            yVals.add(new BarEntry(Servings.getTotalServingsOnDate(day), yVals.size()));
        }

        final BarDataSet dataSet = new BarDataSet(yVals, "Servings");

        dataSet.setColor(ContextCompat.getColor(this, R.color.green));
        dataSet.setValueTextColor(ContextCompat.getColor(this, android.R.color.white));
        dataSet.setValueTextSize(14);

        // We just want the value as an integer
        dataSet.setValueFormatter(new BarChartValueFormatter());

        return new BarData(xVals, dataSet);
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
