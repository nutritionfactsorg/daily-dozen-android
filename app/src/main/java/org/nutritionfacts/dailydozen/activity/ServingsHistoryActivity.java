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
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.ActivityServingsHistoryBinding;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.event.TimeRangeSelectedEvent;
import org.nutritionfacts.dailydozen.event.TimeScaleSelectedEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.task.LoadServingsHistoryTask;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;

import java.util.Date;

public class ServingsHistoryActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {
    private ActivityServingsHistoryBinding binding;

    private boolean alreadyLoadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServingsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTimeRangeSelector();

        loadData();
    }

    private void initTimeRangeSelector() {
        final Day firstDay = Day.getFirstDay();
        final Day lastDay = Day.getLastDay();
        if (firstDay != null && lastDay != null) {
            binding.dailyServingsHistoryTimeRange.setStartAndEnd(
                    firstDay.getYear(), firstDay.getMonth(),
                    lastDay.getYear(), lastDay.getMonth());
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);
        Bus.register(binding.dailyServingsHistoryTimeRange);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.unregister(this);
        Bus.unregister(binding.dailyServingsHistoryTimeRange);
    }

    private void loadData() {
        if (!alreadyLoadingData) {
            alreadyLoadingData = true;

            new LoadServingsHistoryTask(this).execute(new LoadHistoryTaskParams(
                    binding.dailyServingsHistoryTimeScale.getSelectedTimeScale(),
                    binding.dailyServingsHistoryTimeRange.getSelectedYear(),
                    binding.dailyServingsHistoryTimeRange.getSelectedMonth()));
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

        binding.dailyServingsChart.setVisibility(View.VISIBLE);

        binding.dailyServingsChart.setData(chartData);

        // Draw bars behind lines
        binding.dailyServingsChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        binding.dailyServingsChart.setVisibleXRange(5, 5);

        binding.dailyServingsChart.getXAxis().setDrawLabels(true);

        // Without this line, MPAndroidChart v2.1.6 cuts off the tops of the X-axis date labels
        binding.dailyServingsChart.setExtraTopOffset(4f);

        // Start the chart with the latest day in view
        binding.dailyServingsChart.moveViewToX(binding.dailyServingsChart.getXChartMax());

        binding.dailyServingsChart.setDescription("");

        // Prevents the value for each bar from drawing over the labels at the top
        binding.dailyServingsChart.setDrawValueAboveBar(false);

        // Even though we hide the left axis, we must set its max value so that full servings days reach the top
        binding.dailyServingsChart.getAxisLeft().setAxisMinValue(0);
        binding.dailyServingsChart.getAxisLeft().setAxisMaxValue(Common.MAX_SERVINGS);
        binding.dailyServingsChart.getAxisLeft().setEnabled(false);

        binding.dailyServingsChart.getAxisRight().setEnabled(false);

        // Disable all zooming and interacting with the chart
        binding.dailyServingsChart.setScaleEnabled(false);
        binding.dailyServingsChart.setPinchZoom(false);
        binding.dailyServingsChart.setDoubleTapToZoomEnabled(false);
        binding.dailyServingsChart.setHighlightPerDragEnabled(false);

        binding.dailyServingsChart.setOnChartValueSelectedListener(this);

        // Only enable jumping to dates if the user is viewing daily data
        binding.dailyServingsChart.setHighlightPerTapEnabled(event.getTimeScale() == TimeScale.DAYS);

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
