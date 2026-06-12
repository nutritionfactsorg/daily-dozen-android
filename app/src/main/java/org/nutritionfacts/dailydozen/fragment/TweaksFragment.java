package org.nutritionfacts.dailydozen.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.FragmentTweaksBinding;
import org.nutritionfacts.dailydozen.event.TweakServingsChangedEvent;
import org.nutritionfacts.dailydozen.exception.InvalidDateException;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.util.WideScreenLayout;
import org.nutritionfacts.dailydozen.widget.TweakBoxes;
import org.nutritionfacts.dailydozen.widget.TweakGroupHeader;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TweaksFragment extends Fragment {
    private FragmentTweaksBinding binding;

    private Day day;
    private final List<View> sectionItems = new ArrayList<>();

    public static TweaksFragment newInstance(final Day day) {
        final Bundle args = new Bundle();
        args.putString(Args.DATE, day.getDateString());

        final TweaksFragment tweaksFragment = new TweaksFragment();
        tweaksFragment.setArguments(args);
        return tweaksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

                binding.headerTweaks.setDate(day);
                binding.headerTweaks.setServings(TweakServings.getTotalTweakServingsOnDate(day));

                binding.dateWeights.setDay(day);
                Bus.register(binding.dateWeights);

                final Context context = getContext();

                for (Tweak tweak : Tweak.getAllTweaks()) {
                    switch (tweak.getIdName()) {
                        case "Meal Water":
                            flushSectionItems(context);
                            addGroupHeader(context, Common.MEAL);
                            break;
                        case "Daily Black Cumin":
                            flushSectionItems(context);
                            addGroupHeader(context, Common.DAILY);
                            addGroupHeader(context, Common.DAILY_DOSE);
                            break;
                        case "Nightly Fast":
                            flushSectionItems(context);
                            addGroupHeader(context, Common.NIGHTLY);
                            break;
                        default:
                            break;
                    }

                    final TweakBoxes tweakBoxes = new TweakBoxes(context);
                    final boolean success = tweakBoxes.setDateAndTweak(day, tweak);
                    if (success) {
                        sectionItems.add(tweakBoxes);
                        Bus.register(tweakBoxes);
                    }
                }

                flushSectionItems(context);
            } catch (InvalidDateException e) {
                Timber.e(e, "displayFormForDate: ");
            }
        }
    }

    private void addGroupHeader(final Context context, final String group) {
        binding.dateTweaks.addView(createGroupHeader(context, group));
    }

    private void flushSectionItems(final Context context) {
        if (sectionItems.isEmpty()) {
            return;
        }

        final LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        final LinearLayout left = new LinearLayout(context);
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        row.addView(left);

        final LinearLayout right = new LinearLayout(context);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (WideScreenLayout.useTwoColumns(getResources())) {
            final View divider = new View(context);
            divider.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDividerColor));
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    (int) context.getResources().getDisplayMetrics().density,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            row.addView(divider);
            row.addView(right);
        }

        WideScreenLayout.distributeViews(left, right, getResources(), sectionItems);
        binding.dateTweaks.addView(row);
        sectionItems.clear();
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

        WideScreenLayout.unregisterBusInTree(binding.dateTweaks);

        Bus.unregister(binding.dateWeights);
    }

    @Subscribe
    public void onEvent(TweakServingsChangedEvent event) {
        if (event.getDateLong() == day.getDateLong()) {
            Timber.d("onEvent(TweakServingsChangedEvent): date [%s] tweakName [%s]", event.getDateLong(), event.getTweakName());
            binding.headerTweaks.setServings(TweakServings.getTotalTweakServingsOnDate(day));
        }
    }
}
