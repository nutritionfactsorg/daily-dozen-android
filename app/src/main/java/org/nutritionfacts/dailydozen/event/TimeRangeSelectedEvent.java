package org.nutritionfacts.dailydozen.event;

public class TimeRangeSelectedEvent extends BaseEvent {
    private int year;
    private int month;

    public TimeRangeSelectedEvent(final int year, final int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }
}
