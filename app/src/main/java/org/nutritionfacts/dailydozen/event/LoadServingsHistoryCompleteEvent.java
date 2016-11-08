package org.nutritionfacts.dailydozen.event;

import com.github.mikephil.charting.data.CombinedData;

public class LoadServingsHistoryCompleteEvent extends BaseEvent {
    private CombinedData chartData;

    public LoadServingsHistoryCompleteEvent(final CombinedData chartData) {
        this.chartData = chartData;
    }

    public CombinedData getChartData() {
        return chartData;
    }
}
