package org.slavick.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Table(name = "dates")
public class Day extends Model {
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

    private void setDate(Date date) {
        this.date = getDateAsLong(date);

        final Calendar cal = Calendar.getInstance(Locale.getDefault());
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
        return new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(getDateObject());
    }

    public static Day getByDate(Date date) {
        return new Select().from(Day.class).where("date = ?", getDateAsLong(date)).executeSingle();
    }

    public static Day createDateIfDoesNotExist(final Date date) {
        Day day = getByDate(date);

        if (day == null) {
            day = new Day(date);
            day.save();
        }

        return day;
    }
}
