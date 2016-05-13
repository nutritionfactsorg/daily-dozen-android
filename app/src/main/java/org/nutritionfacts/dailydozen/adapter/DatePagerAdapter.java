package org.nutritionfacts.dailydozen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.nutritionfacts.dailydozen.fragment.DateFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private int numDaysSinceEpoch;

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);

        numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
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
        return numDaysSinceEpoch;
    }
}
