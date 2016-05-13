package org.nutritionfacts.dailydozen.fragment;

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
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.event.ShowExplodingStarAnimation;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.widget.DateServings;
import org.nutritionfacts.dailydozen.widget.FoodServings;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import likeanimation.LikeButtonView;

public class DateFragment extends Fragment {
    private static final String TAG = DateFragment.class.getSimpleName();

    @Bind(R.id.back_to_today)
    protected TextView tvBackToToday;
    @Bind(R.id.date_servings)
    protected DateServings dateServings;
    @Bind(R.id.date_food_servings)
    protected ViewGroup vgFoodServings;
    @Bind(R.id.exploding_star_container)
    protected ViewGroup vgExplodingStar;
    @Bind(R.id.exploding_star)
    protected LikeButtonView explodingStar;

    private Day day;

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
        return inflater.inflate(R.layout.fragment_date, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        displayFormForDate();

        Bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
        tvBackToToday.setVisibility(Day.isToday(day) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.back_to_today)
    public void onBackToTodayClicked() {
        Bus.displayLatestDate();
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

    @Subscribe
    public void onEvent(ShowExplodingStarAnimation event) {
        showExplodingStarAnimation();
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

                askUserToRateAfterFirstStarExplosion();
            }
        });
    }

    private void askUserToRateAfterFirstStarExplosion() {
        final Prefs prefs = Prefs.getInstance(getContext());

        if (!prefs.userHasSeenFirstStarExplosion()) {
            Common.askUserToRateApp(getContext());

            prefs.setUserHasSeenFirstStarExplosion();
        }
    }

    private void updateServingsCount() {
        updateServingsCount(Servings.getTotalServingsOnDate(day));
    }

    private void updateServingsCount(final int numServings) {
        dateServings.setServings(numServings);
    }
}
