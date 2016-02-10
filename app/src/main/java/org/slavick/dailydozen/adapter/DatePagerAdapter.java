package org.slavick.dailydozen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.slavick.dailydozen.fragment.DateFragment;
import org.slavick.dailydozen.model.Day;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private int origNumDaysSinceEpoch;

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);

        this.origNumDaysSinceEpoch = Day.getNumDaysSinceEpoch();
    }

    @Override
    public Fragment getItem(int position) {
        return DateFragment.newInstance(Day.getByOffsetFromEpoch(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Day.getTabTitleForDay(position);
    }

    @Override
    public int getCount() {
        final int numDaysSinceEpoch = Day.getNumDaysSinceEpoch();

        // If the app is sent to the background and brought back to the foreground the next day, a crash results when
        // the adapter is found to return a different value from getCount() without notifyDataSetChanged() having been
        // called first. This is an attempt to fix that, but I am not sure that it works.
        // This bug was found by entering some data before bed and then bringing the app back to the foreground in the
        // morning to enter data. The app crashed immediately.
        // Failed attempts:
        // 1. In MainActivity.onResume: datePagerAdapter.notifyDataSetChanged()
        // 2. In MainActivity.onResume: if (daysSinceEpoch < Day.getNumDaysSinceEpoch()) { initDatePager(); }
        if (numDaysSinceEpoch != origNumDaysSinceEpoch) {
            notifyDataSetChanged();

            origNumDaysSinceEpoch = numDaysSinceEpoch;
        }

        return origNumDaysSinceEpoch;
    }
}
