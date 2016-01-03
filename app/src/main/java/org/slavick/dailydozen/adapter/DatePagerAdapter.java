package org.slavick.dailydozen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.fragment.DateFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private DateFormat tabTitleFormatter;

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);

        tabTitleFormatter = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
    }

    @Override
    public Fragment getItem(int position) {
        return DateFragment.newInstance(getDateByOffsetFromBeginning(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleFormatter.format(getDateByOffsetFromBeginning(position));
    }

    @Override
    public int getCount() {
        return Common.getDaysSinceEpoch();
    }

    private Date getDateByOffsetFromBeginning(int position) {
        // Given the position the user is at in the pager, calculate the corresponding date
        return new Date(Common.getEpoch().plusDays(position).getMilliseconds(TimeZone.getDefault()));
    }
}
