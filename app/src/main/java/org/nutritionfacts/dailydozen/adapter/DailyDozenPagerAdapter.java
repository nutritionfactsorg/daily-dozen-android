package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.nutritionfacts.dailydozen.fragment.DailyDozenFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class DailyDozenPagerAdapter extends FragmentStatePagerAdapter {
    private int numDaysSinceEpoch;

    public DailyDozenPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        this.numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DailyDozenFragment.newInstance(Day.getByOffsetFromEpoch(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Day.getTabTitleForDay(position);
    }

    @Override
    public int getCount() {
        return numDaysSinceEpoch;
    }
}
