package org.nutritionfacts.dailydozen.event;

import com.github.mikephil.charting.data.CombinedData;

import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class LoadHistoryCompleteEvent extends BaseEvent {
    private CombinedData chartData;
    @TimeScale.Interface
    private int timeScale;
    private float minVal;
    private float maxVal;

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

    public float getMinVal() {
        return minVal;
    }

    public void setMinVal(float minVal) {
        this.minVal = minVal;
    }

    public float getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(float maxVal) {
        this.maxVal = maxVal;
    }
}
