package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.nutritionfacts.dailydozen.fragment.DailyDozenFragment;
import org.nutritionfacts.dailydozen.fragment.TweaksFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class DatePagerAdapter extends FragmentStateAdapter {
    private final boolean inDailyDozenMode;
    private final int numDaysSinceEpoch;

    public DatePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean inDailyDozenMode) {
        super(fragmentManager, lifecycle);
        this.inDailyDozenMode = inDailyDozenMode;
        this.numDaysSinceEpoch = Day.getNumDaysSinceEpoch();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (inDailyDozenMode) {
            return DailyDozenFragment.newInstance(Day.getByOffsetFromEpoch(position));
        } else {
            return TweaksFragment.newInstance(Day.getByOffsetFromEpoch(position));
        }
    }

    @Override
    public int getItemCount() {
        return numDaysSinceEpoch;
    }
}
