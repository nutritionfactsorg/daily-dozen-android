package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.nutritionfacts.dailydozen.fragment.DailyDozenFragment;
import org.nutritionfacts.dailydozen.fragment.TweaksFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    private final boolean inDailyDozenMode;
    private final int numDaysSinceEpoch;

    public DatePagerAdapter(@NonNull FragmentManager fm, int behavior, boolean inDailyDozenMode) {
        super(fm, behavior);

        this.inDailyDozenMode = inDailyDozenMode;
        this.numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (inDailyDozenMode) {
            return DailyDozenFragment.newInstance(Day.getByOffsetFromEpoch(position));
        } else {
            return TweaksFragment.newInstance(Day.getByOffsetFromEpoch(position));
        }
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
