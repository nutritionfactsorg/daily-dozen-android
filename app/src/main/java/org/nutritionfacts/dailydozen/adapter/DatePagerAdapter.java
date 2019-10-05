package org.nutritionfacts.dailydozen.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
