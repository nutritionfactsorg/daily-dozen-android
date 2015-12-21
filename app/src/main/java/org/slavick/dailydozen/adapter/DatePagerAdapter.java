package org.slavick.dailydozen.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.fragment.DateFragment;

import java.util.Date;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = DatePagerAdapter.class.getSimpleName();

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Date date = getDateByOffsetFromBeginning(position);
        if (date != null) {
            final Bundle args = new Bundle();
            args.putSerializable(Args.DATE_ARG, date);

            final DateFragment dateFragment = new DateFragment();
            dateFragment.setArguments(args);
            return dateFragment;
        }

        return null;
    }

    private Date getDateByOffsetFromBeginning(int position) {
        DateTime dateTime = DateTime.forInstant(0, TimeZone.getDefault());
        dateTime = dateTime.plusDays(position);

        Log.d(TAG, String.format("getDateByOffsetFromBeginning: position [%s], dateTime [%s]", position, dateTime));

        return new Date(dateTime.getMilliseconds(TimeZone.getDefault()));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Date date = getDateByOffsetFromBeginning(position);
        return date != null ? date.toString() : "";
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        final TimeZone timeZone = TimeZone.getDefault();
        return DateTime.forInstant(0, timeZone).numDaysFrom(DateTime.forInstant(new Date().getTime(), timeZone)) + 1;
    }

    public int getIndexOfLastPage() {
        return getCount() - 1;
    }
}
