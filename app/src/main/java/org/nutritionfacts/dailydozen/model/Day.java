package org.nutritionfacts.dailydozen.model;

import android.text.TextUtils;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.nutritionfacts.dailydozen.exception.InvalidDateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

@Table(name = "dates")
public class Day extends TruncatableModel {
    @Column(name = "date", unique = true, index = true)
    private long date;

    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Column(name = "day")
    private int day;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Day() {
    }

    public Day(DateTime date) {
        setDate(date);
    }

    public String getDateString() {
        return getDateString(getDateTime());
    }

    private String getDateString(final DateTime dateTime) {
        return dateTime.format("YYYYMMDD");
    }

    // Calculates the number of days between epoch == 0 (Jan 1, 1970) and now
    public static int getNumDaysSinceEpoch() {
        return getNumDaysSinceEpoch(getToday());
    }

    public static int getNumDaysSinceEpoch(final DateTime date) {
        return getEpoch().numDaysFrom(date) + 1;
    }

    private static DateTime getEpoch() {
        return DateTime.forInstant(0, TimeZone.getDefault());
    }

    public static DateTime getToday() {
        return DateTime.today(TimeZone.getDefault());
    }

    // This method is used for scheduling the reinitialization of the DatePagerAdapter
    public static long getMillisUntilMidnight() {
        final DateTime tomorrow = getToday().plusDays(1);
        return DateTime.now(TimeZone.getDefault()).numSecondsFrom(tomorrow) * 1000;
    }

    public DateTime getDateTime() {
        // NOTE: This method used to be the following line. However, because of a bug in Caldroid, I had to
        // change the implementation to return a DateTime with 0 in the time fields instead of null otherwise the
        // food servings history chart would not show events.
        // return DateTime.forDateOnly(year, month, day);

        return new DateTime(year, month, day, 0, 0, 0, 0);
    }

    private void setDate(DateTime dateTime) {
        this.date = Long.valueOf(getDateString(dateTime));

        this.year = dateTime.getYear();
        this.month = dateTime.getMonth();
        this.day = dateTime.getDay();
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return getDateTime().format("WWW, MMM D", Locale.getDefault());
    }

    public String getDayOfWeek() {
        return getDateTime().format("D (WWW)", Locale.getDefault());
    }

    public static Day getByDate(String dateString) throws InvalidDateException {
        if (TextUtils.isEmpty(dateString) || !dateString.matches("\\d{8}")) {
            throw new InvalidDateException(dateString);
        }

        Day day = new Select().from(Day.class)
                .where("date = ?", dateString)
                .executeSingle();

        if (day == null) {
            day = new Day(fromDateString(dateString));
        }

        return day;
    }

    public static Day createDayIfDoesNotExist(final String dateString) throws InvalidDateException {
        return createDayIfDoesNotExist(getByDate(dateString));
    }

    public static Day createDayIfDoesNotExist(final Day day) {
        if (day != null && day.getId() == null) {
            day.save();
        }

        return day;
    }

    public static List<Day> getAllDays() {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .execute();
    }

    public static List<Day> getLastTwoMonths(int year, int month) {
        final List<Day> daysInCurrentMonth = getDaysInYearAndMonth(year, month);
        final List<Day> daysInPreviousMonth;

        if (month == 1) {
            daysInPreviousMonth = getDaysInYearAndMonth(year - 1, 12);
        } else {
            daysInPreviousMonth = getDaysInYearAndMonth(year, month - 1);
        }

        final List<Day> allDays = new ArrayList<>(daysInCurrentMonth.size() + daysInPreviousMonth.size());
        allDays.addAll(daysInCurrentMonth);
        allDays.addAll(daysInPreviousMonth);

        Collections.sort(allDays, new DayComparator());

        return allDays;
    }

    static List<Day> getDaysInYearAndMonth(final int year, final int monthOneBased) {
        return new Select().from(Day.class)
                .where("year = ?", year)
                .and("month = ?", monthOneBased)
                .execute();
    }

    static List<Day> getDaysInYear(final int year) {
        return new Select().from(Day.class)
                .where("year = ?", year)
                .execute();
    }

    public static Day getFirstDay() {
        return new Select().from(Day.class)
                .orderBy("date ASC")
                .limit(1)
                .executeSingle();
    }

    public static Day getLastDay() {
        return new Select().from(Day.class)
                .orderBy("date DESC")
                .limit(1)
                .executeSingle();
    }

    Day getDayBefore() throws InvalidDateException {
        return Day.getByDate(getDateString(getDateTime().minusDays(1)));
    }

    private static DateTime fromDateString(String dateString) {
        if (!TextUtils.isEmpty(dateString)) {
            return DateTime.forDateOnly(
                    Integer.valueOf(dateString.substring(0, 4)),  // year
                    Integer.valueOf(dateString.substring(4, 6)),  // month
                    Integer.valueOf(dateString.substring(6, 8))); // day
        }

        return null;
    }

    public List<Day> getDaysAfter() {
        return new Select().from(Day.class)
                .where("date >= ?", getDateString(getDateTime()))
                .orderBy("date ASC")
                .execute();
    }

    public static Day getByOffsetFromEpoch(int daysSinceEpoch) {
        return new Day(getEpoch().plusDays(daysSinceEpoch));
    }

    public static String getTabTitleForDay(int daysSinceEpoch) {
        return getEpoch().plusDays(daysSinceEpoch).format("WWW, MMM D", Locale.getDefault());
    }

    public static boolean isToday(final Day day) {
        // NOTE: This method used to be the following single line. However, because of a bug in Caldroid, I had to
        // change the implementation of getDateTime() to return a DateTime with 0 in the time fields instead of null.
        // That broke the following comparison and now I have to manually check if year, month, and day are equal.
        // return day.getDateTime().compareTo(getToday()) == 0;

        final DateTime dateInQuestion = day.getDateTime();
        final DateTime today = getToday();

        return dateInQuestion.getYear().equals(today.getYear()) &&
                dateInQuestion.getMonth().equals(today.getMonth()) &&
                dateInQuestion.getDay().equals(today.getDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day = (Day) o;

        return date == day.date;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = (int) (31 * result + (date ^ (date >>> 32)));
        return result;
    }

    private static class DayComparator implements Comparator<Day> {
        @Override
        public int compare(Day o1, Day o2) {
            return (int) (o1.getDate() - o2.getDate());
        }
    }
}
