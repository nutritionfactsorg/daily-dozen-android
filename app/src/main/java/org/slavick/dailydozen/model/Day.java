package org.slavick.dailydozen.model;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hugo.weaving.DebugLog;

@Table(name = "dates")
public class Day extends Model {
    private final static String TAG = Day.class.getSimpleName();

    public static final String DATE = "date";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    private static final String DAY = "day";

    @Column(name = DATE, unique = true, index = true)
    private long date;

    @Column(name = YEAR)
    private long year;

    @Column(name = MONTH)
    private long month;

    @Column(name = DAY)
    private long day;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public Day() {
    }

    public Day(long date) {
        setDate(date);
    }

    public Day(Date date) {
        setDate(date);
    }

    public static long getDateAsLong(Date date) {
        return Long.valueOf(dateFormat.format(date));
    }

    public Date getDateObject() {
        Date dateObject = null;
        try {
            dateObject = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateObject;
    }

    private void setDate(long date) {
        this.date = date;

        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(getDateObject());

        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
    }

    private void setDate(Date date) {
        setDate(getDateAsLong(date));
    }

    public long getDate() {
        return date;
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public long getDay() {
        return day;
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(getDateObject());
    }

    public static Day getByDate(long date) {
        return new Select().from(Day.class).where("date = ?", date).executeSingle();
    }

    public static Day getByDate(Date date) {
        return getByDate(getDateAsLong(date));
    }

    public static Day createDateIfDoesNotExist(final long date) {
        Day day = getByDate(date);

        if (day == null) {
            day = new Day(date);
            day.save();
        }

        return day;
    }

    public static Day createDateIfDoesNotExist(final Date date) {
        return createDateIfDoesNotExist(getDateAsLong(date));
    }

    public static List<Day> getAllDays() {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .execute();
    }

    @DebugLog
    public static void deleteAllDays() {
        ActiveAndroid.beginTransaction();

        try {
            for (Day day : getAllDays()) {
                Servings.deleteServingsOnDate(day);

                Log.d(TAG, "Deleting " + day);
                day.delete();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static int getCount() {
        return new Select().from(Day.class)
                .count();
    }
}
