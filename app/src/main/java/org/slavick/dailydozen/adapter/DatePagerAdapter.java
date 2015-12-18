package org.slavick.dailydozen.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.fragment.DateFragment;
import org.slavick.dailydozen.model.Day;

public class DatePagerAdapter extends FragmentStatePagerAdapter {
    public DatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Day date = Day.getDateByOffsetFromBeginning(position);
        if (date != null) {
            final Bundle args = new Bundle();
            args.putSerializable(Args.DATE_ARG, date);

            final DateFragment dateFragment = new DateFragment();
            dateFragment.setArguments(args);
            return dateFragment;
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final Day date = Day.getDateByOffsetFromBeginning(position);
        return date != null ? date.toString() : "";
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return Day.getNumDates();
    }

    public int getIndexOfLastPage() {
        return getCount() - 1;
    }
}
