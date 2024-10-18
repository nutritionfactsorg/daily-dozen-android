package org.nutritionfacts.dailydozen.event;

import java.time.LocalDateTime;

public class DisplayDateEvent extends BaseEvent {
    private LocalDateTime date;

    public DisplayDateEvent(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
