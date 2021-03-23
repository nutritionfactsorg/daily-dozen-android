package org.nutritionfacts.dailydozen.event;

public class ReminderRemovedEvent extends BaseEvent {
    private int adapterPosition;

    public ReminderRemovedEvent(int adapterPosition) {
        this.adapterPosition = adapterPosition;
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }
}
