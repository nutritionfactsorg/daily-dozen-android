package org.nutritionfacts.dailydozen.util;

import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
    private final static double AVERAGE_MILLIS_PER_YEAR = 365.24 * 24 * 60 * 60 * 1000;
    private final static double AVERAGE_MILLIS_PER_MONTH = AVERAGE_MILLIS_PER_YEAR / 12;

    public static Calendar getCalendarForYearAndMonth(final int year, final int monthZeroBased) {
        final Calendar cal = getCalendarForToday();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthZeroBased);
        return cal;
    }

    public static Calendar getCalendarForToday() {
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

    // This method is meant to calculate a rough approximation of the number of months between a start date and now.
    // The output is meant only for showing progress when loading data from the database.
    public static int monthsSince(Calendar start) {
        return timeBetween(start, DateUtil.getCalendarForToday(), AVERAGE_MILLIS_PER_MONTH);
    }

    public static int timeBetween(Calendar start, Calendar end, double millis) {
        return (int) ((end.getTime().getTime() - start.getTime().getTime()) / millis);
    }

    public static void addOneMonth(Calendar cal) {
        cal.add(Calendar.MONTH, 1);
    }

    public static void subtractMonths(Calendar cal, int numMonths) {
        cal.add(Calendar.MONTH, 0 - Math.abs(numMonths));
    }

    public static String toStringYYYYMM(Calendar cal) {
        return String.format("%s%s", getYear(cal), getMonthOneBased(cal));
    }
}
