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

    private int daysOfHistory = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servings_history);

        initHistorySpinner();

        new LoadServingsHistoryTask(this, this).execute(daysOfHistory);
    }

    private void initHistorySpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.servings_history_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner historySpinner = (Spinner) findViewById(R.id.daily_servings_spinner);
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

        // Only show 1 week at a time and start the chart with the latest day in view
        chart.setVisibleXRange(daysOfHistory, daysOfHistory);
        chart.moveViewToX(chart.getXChartMax());

        if (daysOfHistory > 30) {
            for (BarLineScatterCandleBubbleDataSet<?> dataSet : chartData.getDataSets()) {
                dataSet.setDrawValues(false);
            }
        }

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
        final String selectedItem = (String) parent.getItemAtPosition(position);

        daysOfHistory = 30;
        if (selectedItem.equalsIgnoreCase("1 month")) {
            daysOfHistory = 30;
        } else if (selectedItem.equals("3 months")) {
            daysOfHistory = 90;
        } else if (selectedItem.equals("6 months")) {
            daysOfHistory = 180;
        } else if (selectedItem.equals("1 year")) {
            daysOfHistory = 365;
        } else if (selectedItem.equals("from the beginning")) {
            daysOfHistory = Integer.MAX_VALUE;
        }

        new LoadServingsHistoryTask(this, this).execute(daysOfHistory);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
