package org.nutritionfacts.dailydozen.util;

import org.nutritionfacts.dailydozen.model.Day;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DateUtil {
    private static Calendar getCalendarForYearMonthAndDay(final int year,
                                                          final int monthOneBased,
                                                          final int day) {
        // We need to subtract one to convert the one-based month arg into a zero-based month
        final Calendar cal = getCalendarForYearAndMonth(year, monthOneBased - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal;
    }

    public static Calendar getCalendarForYearAndMonth(final int year, final int monthZeroBased) {
        final Calendar cal = getCalendarForToday();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthZeroBased);
        return cal;
    }

    private static Calendar getCalendarForToday() {
        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static int getCurrentYear() {
        return getYear(getCalendarForToday());
    }

    public static int getCurrentMonthOneBased() {
        return getMonthOneBased(getCalendarForToday());
    }

    public static int getYear(Calendar cal) {
        return cal.get(Calendar.YEAR);
    }

    public static int getMonthOneBased(Calendar cal) {
        return cal.get(Calendar.MONTH) + 1;
    }

    public static void addOneMonth(Calendar cal) {
        cal.add(Calendar.MONTH, 1);
    }

    public static void subtractTwoMonths(Calendar cal) {
        cal.add(Calendar.MONTH, -2);
    }

    public static String toStringYYYYMM(Calendar cal) {
        return String.format("%s%s", getYear(cal), getMonthOneBased(cal));
    }

    public static String getShortNameOfMonth(final int monthNumberOneBased) {
        return new SimpleDateFormat("MMM", Locale.getDefault())
                .format(getCalendarForYearAndMonth(2016, monthNumberOneBased - 1).getTime());
    }

    public static DateTime convertDateToDateTime(final Date date) {
        return date != null ? DateTime.forInstant(date.getTime(), TimeZone.getDefault()) : null;
    }

    public static Date convertDayToDate(final Day day) {
        return day != null ? getCalendarForYearMonthAndDay(day.getYear(), day.getMonth(), day.getDayNumber()).getTime() : null;
    }
}
