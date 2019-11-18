package org.nutritionfacts.dailydozen.event;

import com.github.mikephil.charting.data.CombinedData;

import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class LoadHistoryCompleteEvent extends BaseEvent {
    private CombinedData chartData;
    @TimeScale.Interface
    private int timeScale;

    public LoadHistoryCompleteEvent(final CombinedData chartData,
                                    final int timeScale) {
        this.chartData = chartData;
        this.timeScale = timeScale;
    }

    public CombinedData getChartData() {
        return chartData;
    }

    @TimeScale.Interface
    public int getTimeScale() {
        return timeScale;
    }
}
