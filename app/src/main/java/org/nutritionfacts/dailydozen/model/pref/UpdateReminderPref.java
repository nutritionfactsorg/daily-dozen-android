package org.nutritionfacts.dailydozen.model.pref;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.nutritionfacts.dailydozen.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

public class UpdateReminderPref {
    // The default Update Reminder notification displays at 8pm
    @SerializedName("hourOfDay")
    private int hourOfDay = 20; // Default to 8pm
    @SerializedName("minute")
    private int minute = 0;
    @SerializedName("reminderTimes")
    private List<String> reminderTimes = new ArrayList<>();

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

    public void addReminderTime(Context context, int hourOfDay, int minute) {
        // Add new reminder to set to eliminate duplicates
        Set<String> reminderTimesSet = new HashSet<>(reminderTimes);
        reminderTimesSet.add(DateUtil.formatTime(context, hourOfDay, minute));
        reminderTimes = new ArrayList<>(reminderTimesSet);

        Collections.sort(reminderTimes, new TimeStringComparator());
    }

    public void deleteReminderTime(int position) {
        reminderTimes.remove(position);
    }

    public List<String> getReminderTimes() {
        return reminderTimes;
    }

    @NonNull
    @Override
    public String toString() {
        return TextUtils.join(", ", reminderTimes);
    }

    private long getAlarmTimeInMillis() {
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

    public long getNextAlarmTimeInMillis(Context context) {
        final Calendar cal = Calendar.getInstance();

        // Convert current calendar time into string format to compare against reminderTimes
        final String currentTime = DateUtil.formatTime(context, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        Timber.d("currentTime: %s", currentTime);

        String nextAlarmTime = "";
        for (String reminderTime : reminderTimes) {
            Timber.d("comparing against %s", reminderTime);
            if (new TimeStringComparator().compare(currentTime, reminderTime) < 0) {
                nextAlarmTime = reminderTime;
                break;
            }
        }

        // nextAlarmTime is empty when the current time is greater than all reminderTimes
        if (nextAlarmTime.isEmpty()) {
            if (reminderTimes.isEmpty()) {
                // Use the default reminder time if the user hasn't configured any custom times
                reminderTimes.add(DateUtil.formatTime(context, getHourOfDay(), getMinute()));
            }
            nextAlarmTime = reminderTimes.get(0);
        }

        Timber.d("next alarm time: %s", nextAlarmTime);

        final Calendar nextAlarmCal = Calendar.getInstance();
        nextAlarmCal.setTimeInMillis(new TimeStringComparator().timeInMillis(nextAlarmTime));

        // Set the UpdateReminderPref's properties to the next alarm hour and minute
        setHourOfDay(nextAlarmCal.get(Calendar.HOUR_OF_DAY));
        setMinute(nextAlarmCal.get(Calendar.MINUTE));

        return getAlarmTimeInMillis();
    }

    private static class TimeStringComparator implements Comparator<String> {
        private final DateFormat twelveHourFormat = new SimpleDateFormat("h:mm a", Locale.US);
        private final DateFormat twentyFourHourFormat = new SimpleDateFormat("H:mm", Locale.getDefault());

        @Override
        public int compare(String time1, String time2) {
            return timeInMillis(time1) - timeInMillis(time2);
        }

        public int timeInMillis(String time) {
            return timeInMillis(time, twelveHourFormat);
        }

        private int timeInMillis(String time, DateFormat format) {
            try {
                Date date = format.parse(time);
                if (date != null) {
                    return (int) date.getTime();
                }
            } catch (ParseException e) {
                if (format != twentyFourHourFormat) {
                    return timeInMillis(time, twentyFourHourFormat);
                } else {
                    Timber.e(e, "could not parse time");
                }
            }
            return 0;
        }
    }
}
