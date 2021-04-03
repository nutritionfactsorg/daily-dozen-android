package org.nutritionfacts.dailydozen.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.FragmentDailyDozenBinding;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.event.ShowExplodingStarAnimation;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.widget.FoodServings;
import org.nutritionfacts.dailydozen.widget.SupplementDivider;

import timber.log.Timber;

public class DailyDozenFragment extends Fragment {
    private FragmentDailyDozenBinding binding;

    private Day day;

    public static DailyDozenFragment newInstance(final Day day) {
        final Bundle args = new Bundle();
        args.putString(Args.DATE, day.getDateString());

        final DailyDozenFragment dailyDozenFragment = new DailyDozenFragment();
        dailyDozenFragment.setArguments(args);
        return dailyDozenFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDailyDozenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayFormForDate();

        onBackToTodayClicked();

        Bus.register(this);
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DATE)) {
            try {
                day = Day.getByDate(args.getString(Args.DATE));

                initBackToTodayButton();

                binding.dateServings.setServings(DDServings.getTotalServingsOnDate(day));

                final Context context = getContext();
                boolean addedSupplementDivider = false;

                for (Food food : Food.getAllFoods()) {
                    final FoodServings foodServings = new FoodServings(context);
                    final boolean success = foodServings.setDateAndFood(day, food);
                    if (success) {
                        if (Common.isSupplement(food) && !addedSupplementDivider) {
                            binding.dateFoodServings.addView(new SupplementDivider(context));
                            addedSupplementDivider = true;
                        }

                        binding.dateFoodServings.addView(foodServings);
                        Bus.register(foodServings);
                    }
                }
            } catch (InvalidDateException e) {
                Timber.e(e, "displayFormForDate: ");
            }
        }
    }

    private void initBackToTodayButton() {
        binding.backToToday.setVisibility(Day.isToday(day) ? View.GONE : View.VISIBLE);
    }

    public void onBackToTodayClicked() {
        binding.backToToday.setOnClickListener(v -> Bus.displayLatestDate());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Bus.unregister(this);

        for (int i = 0; i < binding.dateFoodServings.getChildCount(); i++) {
            Bus.unregister(binding.dateFoodServings.getChildAt(i));
        }
    }

    @Subscribe
    public void onEvent(FoodServingsChangedEvent event) {
        // Vitamins do not count towards the daily servings total, so we ignore when they are checked/unchecked
        if (!event.getIsVitamin() && event.getDateString().equals(day.getDateString())) {
            final int servingsOnDate = DDServings.getTotalServingsOnDate(day);

            Timber.d("onEvent(FoodServingsChangedEvent): dateString [%s] foodName [%s]", event.getDateString(), event.getFoodName());
            binding.dateServings.setServings(servingsOnDate);

            if (servingsOnDate == Common.MAX_SERVINGS) {
                showExplodingStarAnimation();
            }
        }
    }

    @Subscribe
    public void onEvent(ShowExplodingStarAnimation event) {
        showExplodingStarAnimation();
    }

    private void showExplodingStarAnimation() {
        binding.explodingStarContainer.setVisibility(View.VISIBLE);
        binding.explodingStar.runAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                if (binding.explodingStar != null) {
                    binding.explodingStar.cancelAnimation();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (binding.explodingStarContainer != null) {
                    binding.explodingStarContainer.setVisibility(View.GONE);
                }

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
}
