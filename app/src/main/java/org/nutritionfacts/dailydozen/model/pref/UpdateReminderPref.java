package org.nutritionfacts.dailydozen.model.pref;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class UpdateReminderPref {
    // The default Update Reminder notification vibrates the phone and plays a sound at 8pm
    @SerializedName("hourOfDay")
    private int hourOfDay = 20; // Default to 8pm
    @SerializedName("minute")
    private int minute = 0;
    @SerializedName("vibrate")
    private boolean vibrate = true;
    @SerializedName("playSound")
    private boolean playSound = true;

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    @Override
    public String toString() {
        int hour = hourOfDay < 12 ? hourOfDay : hourOfDay % 12;
        if (hour == 0) {
            hour = 12;
        }

        return String.format(Locale.getDefault(), "%s:%02d %s", hour, minute, hourOfDay < 12 ? "AM" : "PM");
    }

    public long getAlarmTimeInMillis() {
        final Calendar cal = Calendar.getInstance();
        final int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = cal.get(Calendar.MINUTE);

        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        // If the alarm time for today has already passed, add 24 hours to set the alarm for tomorrow.
        if (currentHourOfDay > hourOfDay || currentHourOfDay == hourOfDay && currentMinute >= minute) {
            cal.add(Calendar.HOUR, 24);
        }

        Timber.d("getAlarmTimeInMillis %s = %s", cal.getTime(), cal.getTimeInMillis());

        return cal.getTimeInMillis();
    }
}
