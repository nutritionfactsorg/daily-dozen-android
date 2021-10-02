package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.nutritionfacts.dailydozen.model.Day;

public abstract class SynchronizedDatePagerAdapter extends FragmentStatePagerAdapter {
    protected int numDaysSinceEpoch;

    public SynchronizedDatePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
    }

    @Override
    public int getCount() {
        return numDaysSinceEpoch;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Day.getTabTitleForDay(position);
    }
}
