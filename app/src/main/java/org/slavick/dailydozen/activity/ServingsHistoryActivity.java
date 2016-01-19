package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;

import java.util.ArrayList;
import java.util.List;

public class ServingsHistoryActivity extends AppCompatActivity {
    private BarChart dailyServingsChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        initChart();
    }

    private void initChart() {
        dailyServingsChart = (BarChart) findViewById(R.id.daily_servings_chart);

        dailyServingsChart.setData(getChartData());
        dailyServingsChart.setVisibleXRange(7, 7);
        dailyServingsChart.moveViewToX(dailyServingsChart.getXChartMax());
        dailyServingsChart.setDescription("");

        YAxis leftAxis = dailyServingsChart.getAxisLeft();
        leftAxis.setAxisMaxValue(24);
        leftAxis.setLabelCount(24, false);

        dailyServingsChart.getAxisRight().setEnabled(false);

        dailyServingsChart.getLegend().setEnabled(false);

        dailyServingsChart.setPinchZoom(false);
        dailyServingsChart.setDoubleTapToZoomEnabled(false);
    }

    private BarData getChartData() {
        final List<Day> allDays = Day.getAllDays();

        final List<String> xVals = new ArrayList<>(allDays.size());
        final List<BarEntry> yVals = new ArrayList<>(allDays.size());
        final int[] barColors = new int[allDays.size()];

        for (Day day : allDays) {
            xVals.add(day.getDayOfWeekInitial());

            final int totalServingsOnDate = Servings.getTotalServingsOnDate(day);
            final int i = yVals.size();

            yVals.add(new BarEntry(totalServingsOnDate, i));
            barColors[i] = totalServingsOnDate < 24 ? R.color.green_faint : R.color.green;
        }

        final BarDataSet dataSet = new BarDataSet(yVals, "Servings");
        dataSet.setColors(barColors, this);
        dataSet.setDrawValues(false); // turns off the label at the top of each bar

        return new BarData(xVals, dataSet);
    }
}
