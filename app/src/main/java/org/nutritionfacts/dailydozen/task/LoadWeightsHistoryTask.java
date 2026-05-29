package org.nutritionfacts.dailydozen.task;

import android.content.Context;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Weights;
import org.nutritionfacts.dailydozen.task.params.LoadHistoryTaskParams;
import org.nutritionfacts.dailydozen.util.DateUtil;
import org.nutritionfacts.dailydozen.util.HistoryChartHelper;

import java.util.ArrayList;
import java.util.List;

public class LoadWeightsHistoryTask extends BaseTask<LoadHistoryCompleteEvent> {
    private final Context context;
    private final LoadHistoryTaskParams inputParams;

    public LoadWeightsHistoryTask(Context context, LoadHistoryTaskParams inputParams) {
        this.context = context;
        this.inputParams = inputParams;
    }

    @Override
    public LoadHistoryCompleteEvent call() {
        return getChartDataInDays(inputParams);
    }

    @Override
    public void setUiForLoading() {
    }

    @Override
    public void setDataAfterLoading(LoadHistoryCompleteEvent event) {
        Bus.loadHistoryCompleteEvent(event);
    }

    // This method loads the last two months of history into memory, but only shows the selected
    // month. This is because it needs to use the data from the month before to calculate the
    // starting moving average.
    private LoadHistoryCompleteEvent getChartDataInDays(final LoadHistoryTaskParams inputParams) {
        final List<Day> history = Day.getLastTwoMonths(inputParams.getSelectedYear(), inputParams.getSelectedMonth());

        final int numDaysOfHistory = history.size();

        final List<String> xLabels = new ArrayList<>(numDaysOfHistory);
        final List<BarEntry> barEntries = new ArrayList<>(numDaysOfHistory);
        final List<Entry> lineEntries = new ArrayList<>(numDaysOfHistory);

        float previousTrend = 0;
        float minWeight = Float.MAX_VALUE;
        float maxWeight = Float.MIN_VALUE;

        for (int i = 0; i < numDaysOfHistory; i++) {
            final Day day = history.get(i);

            Weights weightsOnDay = Weights.getWeightsOnDay(day);
            Float averageWeight;
            Float barEntryVal = null;

            if (weightsOnDay != null) {
                averageWeight = weightsOnDay.getAverageWeight();
                if (averageWeight != null) {
                    previousTrend = HistoryChartHelper.calculateTrend(previousTrend, averageWeight);
                    barEntryVal = averageWeight;

                    if (averageWeight > 0 && averageWeight < minWeight) {
                        minWeight = averageWeight;
                    }
                    if (averageWeight > maxWeight) {
                        maxWeight = averageWeight;
                    }
                }
            }

            // Only show the past days of history in the selected month and year
            if (day.getYear() == inputParams.getSelectedYear() && day.getMonth() == inputParams.getSelectedMonth()) {
                final int xIndex = xLabels.size();

                xLabels.add(day.getDayOfWeek());

                if (barEntryVal != null) {
                    barEntries.add(new BarEntry(barEntryVal, xIndex));
                }

                final Entry lineEntry = new Entry(previousTrend, xIndex);
                // Here we set the optional data field on the Entry class. This gives the user the
                // ability to tap on a value in the WeightHistoryActivity and be taken to that date
                lineEntry.setData(DateUtil.convertDayToDate(day));
                lineEntries.add(lineEntry);
            }
        }

        final CombinedData combinedData = HistoryChartHelper.lineAndBarData(
                context,
                xLabels,
                lineEntries,
                barEntries,
                context.getString(R.string.average_weight_on_day),
                HistoryChartHelper.BAR_VALUE_FORMAT_ONE_DECIMAL);

        final LoadHistoryCompleteEvent weightHistory =
                HistoryChartHelper.completeEvent(combinedData, inputParams.getTimeScale());
        weightHistory.setMinVal(minWeight);
        weightHistory.setMaxVal(maxWeight);
        return weightHistory;
    }
}
