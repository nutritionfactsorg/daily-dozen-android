package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.view.View;
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
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.enums.HistoryType;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;
import org.nutritionfacts.dailydozen.task.LoadWeightsHistoryTask;
import org.nutritionfacts.dailydozen.task.TaskRunner;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;
import org.nutritionfacts.dailydozen.util.HistoryChartHelper;

public class WeightHistoryActivity extends DailyDozenActivity implements OnChartValueSelectedListener {
    private ActivityServingsHistoryBinding binding;

    private boolean alreadyLoadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServingsHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.dailyServingsHistoryTimeScale.setVisibility(View.GONE);
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

            binding.dailyServingsChart.setVisibility(View.GONE);
            binding.dailyServingsLoadingLabel.setVisibility(View.VISIBLE);
            binding.dailyServingsLoading.setVisibility(View.VISIBLE);

            LoadHistoryTaskParams loadHistoryTaskParams = new LoadHistoryTaskParams(
                    HistoryType.Weights,
                    TimeScale.DAYS,
                    binding.dailyServingsHistoryTimeRange.getSelectedYear(),
                    binding.dailyServingsHistoryTimeRange.getSelectedMonth());
            TaskRunner.getInstance().executeAsync(new LoadWeightsHistoryTask(this, loadHistoryTaskParams));
        }
    }

    @Subscribe
    public void onEvent(TimeRangeSelectedEvent event) {
        loadData();
    }

    @Subscribe
    public void onEvent(LoadHistoryCompleteEvent event) {
        final CombinedData chartData = event.getChartData();
        if (chartData == null) {
            finish();
            return;
        }

        binding.dailyServingsLoadingLabel.setVisibility(View.GONE);
        binding.dailyServingsLoading.setVisibility(View.GONE);
        binding.dailyServingsChart.setVisibility(View.VISIBLE);

        HistoryChartHelper.presentChartData(
                binding.dailyServingsChart,
                this,
                chartData,
                event.getTimeScale(),
                HistoryChartHelper.dynamicWeightAxis(event.getMinVal(), event.getMaxVal()),
                this);

        alreadyLoadingData = false;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        setResult(Args.SELECTABLE_DATE_REQUEST, Common.createShowDateIntent(
                binding.dailyServingsHistoryTimeRange.getSelectedYear(),
                binding.dailyServingsHistoryTimeRange.getSelectedMonth(),
                e.getXIndex() + 1)); // convert x-index (0-based index) to days by adding 1
        finish();
    }

    @Override
    public void onNothingSelected() {

    }
}
