package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.event.TimeRangeSelectedEvent;
import org.nutritionfacts.dailydozen.event.TimeScaleSelectedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.task.LoadWeightsHistoryTask;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;
import org.nutritionfacts.dailydozen.view.TimeRangeSelector;
import org.nutritionfacts.dailydozen.view.TimeScaleSelector;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeightHistoryActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

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

            new LoadWeightsHistoryTask(this).execute(new LoadHistoryTaskParams(
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
    public void onEvent(LoadHistoryCompleteEvent event) {
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

        // Without this line, MPAndroidChart v2.1.6 cuts off the tops of the X-axis date labels
        chart.setExtraTopOffset(4f);

        // Start the chart with the latest day in view
        chart.moveViewToX(chart.getXChartMax());

        chart.setDescription("");

        // Prevents the value for each bar from drawing over the labels at the top
        chart.setDrawValueAboveBar(false);

        // TODO (slavick) needs to adapt to a close vertical range so the difference between days can be seen

        // Even though we hide the left axis, we must set its max value so that full servings days reach the top
//        chart.getAxisLeft().setAxisMinValue(0);
//        chart.getAxisLeft().setAxisMaxValue(Common.MAX_SERVINGS);
        chart.getAxisLeft().setEnabled(false);

        chart.getAxisRight().setEnabled(false);

        // Disable all zooming and interacting with the chart
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        chart.setOnChartValueSelectedListener(this);

        // Only enable jumping to dates if the user is viewing daily data
        chart.setHighlightPerTapEnabled(event.getTimeScale() == TimeScale.DAYS);

        alreadyLoadingData = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        setResult(Args.SELECTABLE_DATE_REQUEST, Common.createShowDateIntent((Date) e.getData()));
        finish();
    }

    @Override
    public void onNothingSelected() {

    }
}
