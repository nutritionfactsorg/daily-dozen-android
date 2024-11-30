package org.nutritionfacts.dailydozen.model;

import static java.time.temporal.ChronoUnit.DAYS;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.nutritionfacts.dailydozen.exception.InvalidDateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

@Table(name = "dates")
public class Day extends TruncatableModel {
    public static final DateTimeFormatter formatYMD = DateTimeFormatter.ofPattern("uuuuMMdd");
    public static final DateTimeFormatter formatTabTitle = DateTimeFormatter.ofPattern("E, MMM d");

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

    public int getDayNumber() {
        return day;
    }

    public Day() {
    }

    public Day(LocalDate date) {
        setDate(date);
    }

    public String getDateString() {
        return getDateString(getDate());
    }

    private String getDateString(final LocalDate date) {
        return date.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    // Calculates the number of days between epoch == 0 (Jan 1, 1970) and now
    public static int getNumDaysSinceEpoch() {
        // Without the plusDays(1) yesterday is considered the latest date
        return getNumDaysSinceEpoch(getToday().plusDays(1));
    }

    public static int getNumDaysSinceEpoch(final LocalDate date) {
        return (int) DAYS.between(LocalDate.of(1970, 1, 1), date);
    }

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public LocalDate getDate() {
        return LocalDate.of(year, month, day);
    }

    private void setDate(LocalDate date) {
        this.date = Long.parseLong(getDateString(date));

        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.day = date.getDayOfMonth();
    }

    public long getDateLong() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return getDate().format(formatTabTitle);
    }

    public String getDayOfWeek() {
        return getDate().format(DateTimeFormatter.ofPattern("d (E)"));
    }

    public static Day getByDate(String dateString) throws InvalidDateException {
        if (TextUtils.isEmpty(dateString) || !dateString.matches("\\d{8}")) {
            throw new InvalidDateException(dateString);
        }

        Day day = new Select().from(Day.class)
                .where("date = ?", dateString)
                .executeSingle();

        if (day == null) {
            day = new Day(LocalDate.parse(dateString, formatYMD));
        }

        return day;
    }

    public static Day createDay(final String dateString) {
        return createDayIfDoesNotExist(new Day(LocalDate.parse(dateString, formatYMD)));
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

        // Sort the days in ascending order
        allDays.sort(new DayComparator());

        return allDays;
    }

    static List<Day> getDaysInYearAndMonth(final int year, final int monthOneBased) {
        final List<Day> daysWithServings = new Select().from(Day.class)
                .where("year = ?", year)
                .and("month = ?", monthOneBased)
                .execute();

        final Map<Integer, Day> daysWithServingsLookup = new HashMap<>(daysWithServings.size());
        for (Day day :
                daysWithServings) {
            daysWithServingsLookup.put(day.getDayNumber(), day);
        }

        final List<Day> daysInYearAndMonth = new ArrayList<>(32);

        final LocalDate today = getToday();
        boolean isCurrentMonth = today.getYear() == year && today.getMonthValue() == monthOneBased;

        try {
            for (int i = 1; i < 32; i++) {
                if (isCurrentMonth && i > today.getDayOfMonth()) {
                    break;
                }

                if (daysWithServingsLookup.containsKey(i)) {
                    daysInYearAndMonth.add(daysWithServingsLookup.get(i));
                } else {
                    daysInYearAndMonth.add(new Day(LocalDate.of(year, monthOneBased, i)));
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Expected exception from getDaysInYearAndMonth (day-of-the-month value exceeds number of days in the month)");
        }

        return daysInYearAndMonth;
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
        return Day.getByDate(getDateString(getDate().minusDays(1)));
    }

    public List<Day> getDaysAfter() {
        return new Select().from(Day.class)
                .where("date >= ?", date)
                .orderBy("date ASC")
                .execute();
    }

    public static Day getByOffsetFromEpoch(int daysSinceEpoch) {
        return new Day(LocalDate.ofEpochDay(daysSinceEpoch));
    }

    public static boolean isToday(final Day day) {
        return day.getDate().equals(getToday());
    }

    public boolean isOneDayAfter(final Day olderDate) {
        return getDate().equals(olderDate.getDate().plusDays(1));
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
            return (int) (o1.getDateLong() - o2.getDateLong());
        }
    }
}
