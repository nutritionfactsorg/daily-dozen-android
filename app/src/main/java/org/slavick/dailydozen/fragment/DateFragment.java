package org.slavick.dailydozen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.FoodServingsAdapter;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.widget.CardViewHeader;
import org.slavick.dailydozen.widget.FoodServings;

import java.util.Date;

public class DateFragment extends Fragment implements FoodServings.ClickListener {
    private Date date;

    protected CardViewHeader cvHeader;
    protected RecyclerView lvFoodServings;

    public static DateFragment newInstance(final Date date) {
        final Bundle args = new Bundle();
        args.putSerializable(Args.DATE, date);

        final DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(args);
        return dateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);

        cvHeader = (CardViewHeader) view.findViewById(R.id.date_fragment_header);
        lvFoodServings = (RecyclerView) view.findViewById(R.id.date_food_servings);

        displayFormForDate();

        return view;
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DATE)) {
            date = (Date) args.getSerializable(Args.DATE);

            if (date != null) {
                initHeader();

                lvFoodServings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                lvFoodServings.setAdapter(new FoodServingsAdapter(this, date));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cvHeader = null;
        lvFoodServings = null;
    }

    @Override
    public void onServingsChanged() {
        updateScore();
    }

    private void initHeader() {
        cvHeader.setHeader(getString(R.string.servings));
        updateScore();
    }

    private void updateScore() {
        cvHeader.setSubHeader(String.format("%s out of 24", Servings.getTotalServingsOnDate(date)));
    }
}
