package org.slavick.dailydozen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.Bus;
import org.slavick.dailydozen.event.FoodServingsChangedEvent;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.widget.DateServings;
import org.slavick.dailydozen.widget.FoodServings;

public class DateFragment extends Fragment {
    private String dateString;

    protected DateServings dateServings;
    protected ViewGroup lvFoodServings;

    public static DateFragment newInstance(final Day day) {
        final Bundle args = new Bundle();
        args.putString(Args.DATE, day.getDateString());

        final DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(args);
        return dateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);

        dateServings = (DateServings) view.findViewById(R.id.date_servings);
        lvFoodServings = (ViewGroup) view.findViewById(R.id.date_food_servings);

        displayFormForDate();

        Bus.register(this);

        return view;
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DATE)) {
            dateString = args.getString(Args.DATE);

            updateServingsCount();

            for (Food food : Food.getAllFoods()) {
                final FoodServings foodServings = new FoodServings(getContext());
                foodServings.setDateAndFood(dateString, food);
                lvFoodServings.addView(foodServings);

                Bus.register(foodServings);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bus.unregister(this);

        for (int i = 0; i < lvFoodServings.getChildCount(); i++) {
            Bus.unregister(lvFoodServings.getChildAt(i));
        }

        dateServings = null;
    }

    public void onEvent(FoodServingsChangedEvent event) {
        if (event.getDate().getDateString().equals(dateString)) {
            updateServingsCount();
        }
    }

    private void updateServingsCount() {
        dateServings.setServings(Servings.getTotalServingsOnDate(Day.getByDate(dateString)));
    }
}
