package org.nutritionfacts.dailydozen.fragment;

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
import org.nutritionfacts.dailydozen.databinding.FragmentTweaksBinding;
import org.nutritionfacts.dailydozen.event.TweakServingsChangedEvent;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.widget.TweakBoxes;
import org.nutritionfacts.dailydozen.widget.TweakGroupHeader;

import timber.log.Timber;

public class TweaksFragment extends Fragment {
    private FragmentTweaksBinding binding;

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
        binding = FragmentTweaksBinding.inflate(inflater, container, false);
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

                binding.headerTweaks.setServings(TweakServings.getTotalTweakServingsOnDate(day));

                binding.dateWeights.setDay(day);
                Bus.register(binding.dateWeights);

                final Context context = getContext();

                for (Tweak tweak : Tweak.getAllTweaks()) {
                    switch (tweak.getIdName()) {
                        case "Meal Water":
                            binding.dateTweaks.addView(createGroupHeader(context, Common.MEAL));
                            break;
                        case "Daily Black Cumin":
                            binding.dateTweaks.addView(createGroupHeader(context, Common.DAILY));
                            binding.dateTweaks.addView(createGroupHeader(context, Common.DAILY_DOSE));
                            break;
                        case "Nightly Fast":
                            binding.dateTweaks.addView(createGroupHeader(context, Common.NIGHTLY));
                            break;
                        default:
                            break;
                    }

                    final TweakBoxes tweakBoxes = new TweakBoxes(context);
                    final boolean success = tweakBoxes.setDateAndTweak(day, tweak);
                    if (success) {
                        binding.dateTweaks.addView(tweakBoxes);
                        Bus.register(tweakBoxes);
                    }
                }
            } catch (InvalidDateException e) {
                Timber.e(e, "displayFormForDate: ");
            }
        }
    }

    private TweakGroupHeader createGroupHeader(final Context context, final String group) {
        final TweakGroupHeader groupHeader = new TweakGroupHeader(context);
        groupHeader.setTweakGroup(group);
        return groupHeader;
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

        for (int i = 0; i < binding.dateTweaks.getChildCount(); i++) {
            Bus.unregister(binding.dateTweaks.getChildAt(i));
        }

        Bus.unregister(binding.dateWeights);
    }

    @Subscribe
    public void onEvent(TweakServingsChangedEvent event) {
        if (event.getDateString().equals(day.getDateString())) {
            Timber.d("onEvent(TweakServingsChangedEvent): dateString [%s] tweakName [%s]", event.getDateString(), event.getTweakName());
            binding.headerTweaks.setServings(TweakServings.getTotalTweakServingsOnDate(day));
        }
    }
}
