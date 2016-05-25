package org.nutritionfacts.dailydozen.model.pref;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Locale;

public class UpdateReminderPref {
    @SerializedName("hourOfDay")
    private int hourOfDay;
    @SerializedName("minute")
    private int minute;

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

    @Override
    public String toString() {
        int hour = hourOfDay < 12 ? hourOfDay : hourOfDay % 12;
        if (hour == 0) {
            hour = 12;
        }

        return String.format(Locale.getDefault(), "%s:%02d %s", hour, minute, hourOfDay < 12 ? "AM" : "PM");
    }

    public Calendar toCalendar() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }
}
