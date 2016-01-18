package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
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

        dailyServingsChart.setPinchZoom(false);
        dailyServingsChart.setDoubleTapToZoomEnabled(false);
    }

    private BarData getChartData() {
        final List<Day> allDays = Day.getAllDays();

        final List<String> xVals = new ArrayList<>(allDays.size());
        final List<BarEntry> yVals = new ArrayList<>(allDays.size());

        for (Day day : allDays) {
            xVals.add(day.getDayOfWeekInitial());
            yVals.add(new BarEntry(Servings.getTotalServingsOnDate(day), yVals.size()));
        }

        return new BarData(xVals, new BarDataSet(yVals, "Servings"));
    }
}
