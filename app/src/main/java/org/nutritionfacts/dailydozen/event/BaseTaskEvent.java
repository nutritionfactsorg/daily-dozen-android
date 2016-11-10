package org.nutritionfacts.dailydozen.event;

public class BaseTaskEvent extends BaseEvent {
    private final boolean success;

    public BaseTaskEvent(final boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
