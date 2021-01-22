package org.nutritionfacts.dailydozen.event;

public class TweakServingsChangedEvent extends BaseEvent {
    private final String dateString;
    private final String tweakName;

    public TweakServingsChangedEvent(String dateString, String tweakName) {
        this.dateString = dateString;
        this.tweakName = tweakName;
    }

    public String getDateString() {
        return dateString;
    }

    public String getTweakName() {
        return tweakName;
    }
}
