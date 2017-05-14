package org.nutritionfacts.dailydozen.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {
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
}
