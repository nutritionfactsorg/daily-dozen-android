package org.nutritionfacts.dailydozen.event;

import java.time.LocalDate;

public class DisplayDateEvent extends BaseEvent {
    private final LocalDate date;

    public DisplayDateEvent(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
