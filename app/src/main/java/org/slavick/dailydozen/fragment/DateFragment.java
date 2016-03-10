package org.slavick.dailydozen.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.Bus;
import org.slavick.dailydozen.event.FoodServingsChangedEvent;
import org.slavick.dailydozen.exception.InvalidDateException;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.widget.DateServings;
import org.slavick.dailydozen.widget.FoodServings;

import likeanimation.LikeButtonView;

public class DateFragment extends Fragment {
    private static final String TAG = DateFragment.class.getSimpleName();

    private Day day;

    private TextView tvBackToToday;
    private DateServings dateServings;
    private ViewGroup vgFoodServings;
    private ViewGroup vgExplodingStar;
    private LikeButtonView explodingStar;

    public static DateFragment newInstance(final Day day) {
        final Bundle args = new Bundle();
        args.putString(Args.DAY, day.getDateString());

        final DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(args);
        return dateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);

        tvBackToToday = (TextView) view.findViewById(R.id.back_to_today);
        dateServings = (DateServings) view.findViewById(R.id.date_servings);
        vgFoodServings = (ViewGroup) view.findViewById(R.id.date_food_servings);
        vgExplodingStar = (ViewGroup) view.findViewById(R.id.exploding_star_container);
        explodingStar = (LikeButtonView) view.findViewById(R.id.exploding_star);

        displayFormForDate();

        Bus.register(this);

        return view;
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DAY)) {
            try {
                day = Day.getByDate(args.getString(Args.DAY));

                initBackToTodayButton();

                updateServingsCount();

                for (Food food : Food.getAllFoods()) {
                    final FoodServings foodServings = new FoodServings(getContext());
                    foodServings.setDateAndFood(day, food);
                    vgFoodServings.addView(foodServings);

                    Bus.register(foodServings);
                }
            } catch (InvalidDateException e) {
                Log.e(TAG, "displayFormForDate: ", e);
            }
        }
    }

    private void initBackToTodayButton() {
        if (Day.isToday(day)) {
            tvBackToToday.setVisibility(View.GONE);
        } else {
            tvBackToToday.setVisibility(View.VISIBLE);

            tvBackToToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bus.displayLatestDate();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bus.unregister(this);

        for (int i = 0; i < vgFoodServings.getChildCount(); i++) {
            Bus.unregister(vgFoodServings.getChildAt(i));
        }

        dateServings = null;
    }

    @Subscribe
    public void onEvent(FoodServingsChangedEvent event) {
        if (event.getDateString().equals(day.getDateString())) {
            final int servingsOnDate = Servings.getTotalServingsOnDate(day);

            updateServingsCount(servingsOnDate);

            if (servingsOnDate == 24) {
                showExplodingStarAnimation();
            }
        }
    }

    private void showExplodingStarAnimation() {
        vgExplodingStar.setVisibility(View.VISIBLE);
        explodingStar.runAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                explodingStar.cancelAnimation();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vgExplodingStar.setVisibility(View.GONE);
            }
        });
    }

    private void updateServingsCount() {
        updateServingsCount(Servings.getTotalServingsOnDate(day));
    }

    private void updateServingsCount(final int numServings) {
        dateServings.setServings(numServings);
    }
}
