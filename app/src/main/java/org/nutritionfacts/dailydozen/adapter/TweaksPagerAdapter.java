package org.nutritionfacts.dailydozen.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.nutritionfacts.dailydozen.fragment.TweaksFragment;
import org.nutritionfacts.dailydozen.model.Day;

public class TweaksPagerAdapter extends SynchronizedDatePagerAdapter {
    public TweaksPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TweaksFragment.newInstance(Day.getByOffsetFromEpoch(position));
    }
}
