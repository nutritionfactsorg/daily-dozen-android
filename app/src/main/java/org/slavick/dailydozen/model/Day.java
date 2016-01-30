package org.slavick.dailydozen.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;
import java.util.Locale;

import hirondelle.date4j.DateTime;

@Table(name = "dates")
public class Day extends TruncatableModel {
    private final static String TAG = Day.class.getSimpleName();

    public static final String DATE = "date";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    private static final String DAY = "day";

    @Column(name = DATE, unique = true, index = true)
    private long date;

    @Column(name = YEAR)
    private int year;

    @Column(name = MONTH)
    private int month;

    @Column(name = DAY)
    private int day;

    public Day() {
    }

    public Day(DateTime date) {
        setDate(date);
    }

    public static long getDateAsLong(DateTime dateTime) {
        return Long.valueOf(dateTime.format("YYYYMMDD"));
    }

    public DateTime getDateObject() {
        return DateTime.forDateOnly(year, month, day);
    }

    private void setDate(DateTime dateTime) {
        this.date = getDateAsLong(dateTime);

        this.year = dateTime.getYear();
        this.month = dateTime.getMonth();
        this.day = dateTime.getDay();
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return getDateObject().format("WWW, MMM D", Locale.getDefault());
    }

    public String getDayOfWeek() {
        return getDateObject().format("WWW", Locale.getDefault());
    }

    public static Day getByDate(long date) {
        return new Select().from(Day.class).where("date = ?", date).executeSingle();
    }

    public static Day getByDate(DateTime date) {
        return getByDate(getDateAsLong(date));
    }

    public static Day createDateIfDoesNotExist(final DateTime date) {
        Day day = getByDate(date);

        if (day == null) {
            day = new Day(date);
            day.save();
        }

        return day;
    }

    public static List<Day> getAllDays() {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .execute();
    }

    public static int getCount() {
        return new Select().from(Day.class)
                .count();
    }

    public static boolean isEmpty() {
        return getCount() == 0;
    }

    public static List<Day> getDaysAfter(final DateTime date) {
        return new Select().from(Day.class)
                .where("date >= ?", getDateAsLong(date))
                .orderBy("date ASC")
                .execute();
    }

    public static Day getEarliestDay() {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .limit(1)
                .executeSingle();
    }

    public DateTime getDayBefore() {
        return getDateObject().minusDays(1);
    }

    public static DateTime fromDateString(String dateString) {
        String year = dateString.substring(0, 4);
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);

        return DateTime.forDateOnly(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
    }
}
