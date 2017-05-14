package org.nutritionfacts.dailydozen.event;

import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class TimeScaleSelectedEvent extends BaseEvent {
    private int selectedTimeScale;

    public TimeScaleSelectedEvent(@TimeScale.Interface int selectedTimeScale) {
        this.selectedTimeScale = selectedTimeScale;
    }

    @TimeScale.Interface
    public int getSelectedTimeScale() {
        return selectedTimeScale;
    }
}
