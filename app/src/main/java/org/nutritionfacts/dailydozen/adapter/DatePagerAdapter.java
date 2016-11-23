package org.nutritionfacts.dailydozen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.nutritionfacts.dailydozen.fragment.DateFragment;
import org.nutritionfacts.dailydozen.model.Day;

import hirondelle.date4j.DateTime;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private int numDaysSinceEpoch;
    public int numDaysSinceEpochToDay;

    public DatePagerAdapter(FragmentManager fm) {
        super(fm);
        setDates(Day.getNumDaysSinceEpoch());
    }
    
    public DatePagerAdapter(FragmentManager fm, DateTime dt) {
        super(fm);
        setDates(Day.getNumDaysSinceEpoch(dt));
    }
    
    private void setDates(int days){
        numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
        numDaysSinceEpochToDay = days;
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
