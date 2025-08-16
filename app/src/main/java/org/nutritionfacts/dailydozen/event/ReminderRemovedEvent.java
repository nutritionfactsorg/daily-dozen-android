package org.nutritionfacts.dailydozen.event;

public class ReminderRemovedEvent extends BaseEvent {
    private final int adapterPosition;

    public ReminderRemovedEvent(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }
}
