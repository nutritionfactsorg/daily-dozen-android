package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.CombinedData;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.enums.TimeScale;
import org.slavick.dailydozen.task.LoadServingsHistoryTask;

public class ServingsHistoryActivity extends AppCompatActivity
        implements LoadServingsHistoryTask.Listener, AdapterView.OnItemSelectedListener {

    private Spinner historySpinner;

    private boolean alreadyLoadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        initHistorySpinner();
        loadData();
    }

    private void loadData() {
        if (!alreadyLoadingData) {
            alreadyLoadingData = true;
            new LoadServingsHistoryTask(this, this).execute(getSelectedDaysOfHistory());
        }
    }

    @TimeScale.Interface
    private int getSelectedDaysOfHistory() {
        switch (historySpinner.getSelectedItemPosition()) {
            case 0:
            default:
                return TimeScale.DAYS;
            case 1:
                return TimeScale.MONTHS;
            case 2:
                return TimeScale.YEARS;
        }
    }

    private void initHistorySpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.servings_time_scale_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        historySpinner = (Spinner) findViewById(R.id.daily_servings_spinner);
        historySpinner.setOnItemSelectedListener(this);
        historySpinner.setAdapter(adapter);
    }

    @Override
    public void onLoadServings(CombinedData chartData) {
        CombinedChart chart = (CombinedChart) findViewById(R.id.daily_servings_chart);
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
    public void onLoadServingsCancelled() {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
