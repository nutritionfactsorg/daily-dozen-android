package org.nutritionfacts.dailydozen.event;

public class CalculateStreaksTaskCompleteEvent extends BaseTaskEvent {
    public CalculateStreaksTaskCompleteEvent(boolean success) {
        super(success);
    }
}
