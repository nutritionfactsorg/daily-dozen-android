package org.slavick.dailydozen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Date;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.widget.FoodServings;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateFragment extends Fragment {
    public final static String DATE_ARG = "date";

    private Date date;

    @Bind(R.id.date_form)
    protected ViewGroup form;

    @Bind(R.id.date)
    protected TextView tvDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);
        ButterKnife.bind(this, view);

        getDateArgument();

        createFoodServingsWidgets();

        return view;
    }

    private void getDateArgument() {
        if (getArguments() != null && getArguments().containsKey(DATE_ARG)) {
            this.date = (Date) getArguments().getSerializable(DATE_ARG);
        }
    }

    private void createFoodServingsWidgets() {
        final String[] names = getResources().getStringArray(R.array.food_names);
        final int[] quantities = getResources().getIntArray(R.array.food_quantities);

        Food.ensureAllFoodsExistInDatabase(names, quantities);

        tvDate.setText(date.toString());

        for (String name : names) {
            form.addView(new FoodServings(getContext(), date, Food.getByName(name)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
