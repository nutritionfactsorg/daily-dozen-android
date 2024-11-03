package org.nutritionfacts.dailydozen.event;

public class TweakServingsChangedEvent extends BaseEvent {
    private final long dateLong;
    private final String tweakName;

    public TweakServingsChangedEvent(long dateLong, String tweakName) {
        this.dateLong = dateLong;
        this.tweakName = tweakName;
    }

    public long getDateLong() {
        return dateLong;
    }

    public String getTweakName() {
        return tweakName;
    }
}
