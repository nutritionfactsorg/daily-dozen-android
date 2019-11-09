package org.nutritionfacts.dailydozen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.widget.DateWeights;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class TweaksFragment extends Fragment {
    @BindView(R.id.date_weights)
    protected DateWeights dateWeights;
    @BindView(R.id.back_to_today)
    protected TextView tvBackToToday;

    private Unbinder unbinder;

    private Day day;

    public static TweaksFragment newInstance(final Day day) {
        final Bundle args = new Bundle();
        args.putString(Args.DATE, day.getDateString());

        final TweaksFragment tweaksFragment = new TweaksFragment();
        tweaksFragment.setArguments(args);
        return tweaksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweaks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        displayFormForDate();

//        Bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DATE)) {
            try {
                day = Day.getByDate(args.getString(Args.DATE));

                initBackToTodayButton();

                updateWeights();

//                final Context context = getContext();
//                boolean addedSupplementDivider = false;
//
//                for (Food food : Food.getAllFoods()) {
//                    final FoodServings foodServings = new FoodServings(context);
//                    final boolean success = foodServings.setDateAndFood(day, food);
//                    if (success) {
//                        if (Common.isSupplement(food) && !addedSupplementDivider) {
//                            vgFoodServings.addView(new SupplementDivider(context));
//                            addedSupplementDivider = true;
//                        }
//
//                        vgFoodServings.addView(foodServings);
//                        Bus.register(foodServings);
//                    }
//                }
            } catch (InvalidDateException e) {
                Timber.e(e, "displayFormForDate: ");
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

//        Bus.unregister(this);

//        for (int i = 0; i < vgFoodServings.getChildCount(); i++) {
//            Bus.unregister(vgFoodServings.getChildAt(i));
//        }

        dateWeights = null;
    }

//    private void updateServingsCount() {
//        updateServingsCount(Servings.getTotalServingsOnDate(day));
//    }
//
//    private void updateServingsCount(final int numServings) {
//        dateServings.setServings(numServings);
//    }

    private void updateWeights() {
        dateWeights.setDay(day);
        Bus.register(dateWeights);
    }
}
