package org.slavick.dailydozen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.slavick.dailydozen.fragment.DateFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private DateFormat tabTitleFormatter;

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);

        tabTitleFormatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    }

    @Override
    public Fragment getItem(int position) {
        final Date date = getDateByOffsetFromBeginning(position);
        return date != null ? DateFragment.newInstance(date) : null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Date date = getDateByOffsetFromBeginning(position);
        return date != null ? tabTitleFormatter.format(date) : "";
    }

    @Override
    public int getCount() {
        final TimeZone timeZone = TimeZone.getDefault();

        // Calculates the number of days between epoch == 0 (Jan 1, 1970) and now
        return DateTime.forInstant(0, timeZone).numDaysFrom(DateTime.forInstant(new Date().getTime(), timeZone)) + 1;
    }

    private Date getDateByOffsetFromBeginning(int position) {
        final TimeZone timeZone = TimeZone.getDefault();

        // Given the position the user is at in the pager, calculate the corresponding date
        return new Date(DateTime.forInstant(0, timeZone).plusDays(position).getMilliseconds(timeZone));
    }
}
