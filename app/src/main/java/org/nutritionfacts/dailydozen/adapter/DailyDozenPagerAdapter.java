package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.nutritionfacts.dailydozen.fragment.DailyDozenFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class DailyDozenPagerAdapter extends SynchronizedDatePagerAdapter {
    public DailyDozenPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DailyDozenFragment.newInstance(Day.getByOffsetFromEpoch(position));
    }
}
