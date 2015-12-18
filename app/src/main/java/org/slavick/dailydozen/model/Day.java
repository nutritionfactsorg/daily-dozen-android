package org.slavick.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Table(name = "dates")
public class Day extends Model implements Serializable {
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

    public Day() {
    }

    public Day(Date date) {
        setDate(date);
    }

    public static long getDateAsLong(Date date) {
        return Long.valueOf(new SimpleDateFormat("yyyyMMdd", Locale.US).format(date));
    }

    private void setDate(Date date) {
        this.date = getDateAsLong(date);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
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
        String dateString = "";

        try {
            Date javaDate = new SimpleDateFormat("yyyyMMdd", Locale.US).parse(String.valueOf(date));
            dateString = new SimpleDateFormat("EEE, MMM d", Locale.US).format(javaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static Day getByDate(Date date) {
        return new Select().from(Day.class).where("date = ?", getDateAsLong(date)).executeSingle();
    }

    public static Day createToday() {
        return createDateIfDoesNotExist(new Date());
    }

    private static Day createDateIfDoesNotExist(final Date date) {
        Day day = getByDate(date);

        if (day == null) {
            day = new Day(date);
            day.save();
        }

        return day;
    }

    public static int getNumDates() {
        return new Select().from(Day.class).count();
    }

    public static Day getDateByOffsetFromBeginning(final int offset) {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .offset(offset)
                .executeSingle();
    }
}
