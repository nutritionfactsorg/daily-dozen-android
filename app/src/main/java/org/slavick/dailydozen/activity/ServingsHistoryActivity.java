package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.data.CombinedData;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.task.LoadServingsHistoryTask;

public class ServingsHistoryActivity extends AppCompatActivity
        implements LoadServingsHistoryTask.Listener, AdapterView.OnItemSelectedListener {

    private Spinner historySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        initHistorySpinner();
        loadData();
    }

    private void loadData() {
        new LoadServingsHistoryTask(this, this).execute(getSelectedDaysOfHistory());
    }

    private int getSelectedDaysOfHistory() {
        switch (historySpinner.getSelectedItemPosition()) {
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 365;
            case 4:
                return Integer.MAX_VALUE;
            case 0:
            default:
                return 30;
        }
    }

    private void initHistorySpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.servings_history_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        historySpinner = (Spinner) findViewById(R.id.daily_servings_spinner);
        historySpinner.setOnItemSelectedListener(this);
        historySpinner.setAdapter(adapter);
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

        final int daysOfHistory = chartData.getLineData().getXValCount();

        if (daysOfHistory > 30) {
            chart.setVisibleXRange(daysOfHistory, daysOfHistory);

            chart.getXAxis().setDrawLabels(false);

            // Hide labels as they won't be legible anyways
            for (BarLineScatterCandleBubbleDataSet<?> dataSet : chartData.getDataSets()) {
                dataSet.setDrawValues(false);
            }
        } else {
            // Only show 1 week at a time
            chart.setVisibleXRange(7, 7);

            chart.getXAxis().setDrawLabels(true);
        }

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
