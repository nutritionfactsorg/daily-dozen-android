package org.slavick.dailydozen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.widget.FoodServings;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateFragment extends Fragment {

    @Bind(R.id.date_form)
    protected ViewGroup form;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);
        ButterKnife.bind(this, view);

        displayFormForDate();

        return view;
    }

    private void displayFormForDate() {
        if (getArguments() != null && getArguments().containsKey(Args.DATE_ARG)) {
//            final Day date = (Day) getArguments().getSerializable(Args.DATE_ARG);
//            if (date != null) {
//                createFoodServingsWidgets(date);
//            }
        }
    }

    private void createFoodServingsWidgets(final Day date) {
        for (Food food : Food.getAllFoods()) {
            form.addView(new FoodServings(getContext(), date, food));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
