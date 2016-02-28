package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.task.LoadServingsHistoryTask;

public class ServingsHistoryActivity extends AppCompatActivity implements LoadServingsHistoryTask.Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        new LoadServingsHistoryTask(this, this).execute(30);
    }

    @Override
    public void onLoadServings(CombinedData chartData) {
        final CombinedChart chart = (CombinedChart) findViewById(R.id.daily_servings_chart);
        chart.setVisibility(View.VISIBLE);

        chart.setData(chartData);

        // Draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

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

        // Disable all zooming and interacting with the chart
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
    }

    @Override
    public void onLoadServingsCancelled() {
        finish();
    }
}
