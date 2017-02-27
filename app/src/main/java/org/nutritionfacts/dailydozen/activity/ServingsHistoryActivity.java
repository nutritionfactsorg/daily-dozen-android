package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.LoadServingsHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.event.TimeRangeSelectedEvent;
import org.nutritionfacts.dailydozen.event.TimeScaleSelectedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.task.LoadServingsHistoryTask;
import org.nutritionfacts.dailydozen.task.params.LoadServingsHistoryTaskParams;
import org.nutritionfacts.dailydozen.view.TimeRangeSelector;
import org.nutritionfacts.dailydozen.view.TimeScaleSelector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServingsHistoryActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.daily_servings_history_time_scale)
    protected TimeScaleSelector timeScaleSelector;
    @BindView(R.id.daily_servings_history_time_range)
    protected TimeRangeSelector timeRangeSelector;
    @BindView(R.id.daily_servings_chart)
    protected CombinedChart chart;

    private boolean alreadyLoadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);
        ButterKnife.bind(this);

        initTimeRangeSelector();

        loadData();
    }

    private void initTimeRangeSelector() {
        final Day firstDay = Day.getFirstDay();
        final Day lastDay = Day.getLastDay();
        timeRangeSelector.setStartAndEnd(
                firstDay.getYear(), firstDay.getMonth(),
                lastDay.getYear(), lastDay.getMonth());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);
        Bus.register(timeRangeSelector);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.unregister(this);
        Bus.unregister(timeRangeSelector);
    }

    private void loadData() {
        if (!alreadyLoadingData) {
            alreadyLoadingData = true;

            new LoadServingsHistoryTask(this).execute(new LoadServingsHistoryTaskParams(
                    timeScaleSelector.getSelectedTimeScale(),
                    timeRangeSelector.getSelectedYear(),
                    timeRangeSelector.getSelectedMonth()));
        }
    }

    @Subscribe
    public void onEvent(TimeRangeSelectedEvent event) {
        loadData();
    }

    @Subscribe
    public void onEvent(TimeScaleSelectedEvent event) {
        loadData();
    }

    @Subscribe
    public void onEvent(LoadServingsHistoryCompleteEvent event) {
        final CombinedData chartData = event.getChartData();
        if (chartData == null) {
            finish();
            return;
        }

        chart.setVisibility(View.VISIBLE);

        chart.setData(chartData);

        // Draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        chart.setVisibleXRange(5, 5);

        chart.getXAxis().setDrawLabels(true);

        // Start the chart with the latest day in view
        chart.moveViewToX(chart.getXChartMax());

        chart.setDescription("");

        // Prevents the value for each bar from drawing over the labels at the top
        chart.setDrawValueAboveBar(false);

        // Even though we hide the left axis, we must set its max value so that full servings days reach the top
        chart.getAxisLeft().setAxisMinValue(0);
        chart.getAxisLeft().setAxisMaxValue(24);
        chart.getAxisLeft().setEnabled(false);

        chart.getAxisRight().setEnabled(false);

        // Disable all zooming and interacting with the chart
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        alreadyLoadingData = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
