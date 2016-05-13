package org.nutritionfacts.dailydozen.event;

import hirondelle.date4j.DateTime;

public class DisplayDateEvent extends BaseEvent {
    private DateTime date;

    public DisplayDateEvent(DateTime date) {
        this.date = date;
    }

    public DateTime getDate() {
        return date;
    }
}
