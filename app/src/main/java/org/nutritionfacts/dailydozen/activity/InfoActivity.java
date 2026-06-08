package org.nutritionfacts.dailydozen.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.FoodServingsAdapter;
import org.nutritionfacts.dailydozen.adapter.FoodTypeAdapter;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.ActivityInfoBinding;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.model.enums.Units;
import org.nutritionfacts.dailydozen.util.CalendarHistoryDecorator;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfoActivity extends DailyDozenActivity {
    private ActivityInfoBinding binding;

    private Food food;
    private Tweak tweak;

    private boolean isFoodHistory = false;

    private final Set<String> loadedMonths = new HashSet<>();
    private List<LocalDate> fullServingsDates;
    private List<LocalDate> partialServingsDates;

    private Food getFood() {
        return food;
    }

    private Tweak getTweak() {
        return tweak;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFoodOrTweakFromIntent();
        if (food == null && tweak == null) {
            finish();
            return;
        }

        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fullServingsDates = new ArrayList<>();
        partialServingsDates = new ArrayList<>();
        if (savedInstanceState != null) {
            fullServingsDates = (ArrayList<LocalDate>) savedInstanceState.getSerializable(Args.DATES_WITH_FULL_SERVINGS);
            partialServingsDates = (ArrayList<LocalDate>) savedInstanceState.getSerializable(Args.DATES_WITH_PARTIAL_SERVINGS);
        }

        if (getFood() != null) {
            displayFoodInfo();
            displayHistory();
        } else if (getTweak() != null) {
            displayTweakInfo();
            displayHistory();
        }

        if (shouldScrollToHistory()) {
            scrollToHistorySection();
        }
    }

    private void loadFoodOrTweakFromIntent() {
        final Intent intent = getIntent();
        if (intent != null) {
            food = Food.getById(intent.getLongExtra(Args.FOOD_ID, -1));
            if (food != null) {
                setTitle(food.getName());
                return;
            }

            tweak = Tweak.getById(intent.getLongExtra(Args.TWEAK_ID, -1));
            if (tweak != null) {
                setTitle(tweak.getName());
            }
        }
    }

    private boolean shouldScrollToHistory() {
        final Intent intent = getIntent();
        return intent != null && intent.getBooleanExtra(Args.SCROLL_TO_HISTORY, false);
    }

    private void scrollToHistorySection() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }

        binding.historyScroll.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private int layoutPasses;

                    @Override
                    public void onGlobalLayout() {
                        binding.historyScroll.smoothScrollTo(0, binding.historySection.getTop());
                        if (++layoutPasses >= 2) {
                            binding.historyScroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getFood() != null) {
            getMenuInflater().inflate(R.menu.food_info_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.food_info_videos) {
            openFoodVideosInBrowser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFoodInfo() {
        binding.foodInfoSection.setVisibility(View.VISIBLE);
        binding.tweakInfoSection.setVisibility(View.GONE);

        final Food food = getFood();
        if (food == null || TextUtils.isEmpty(food.getName())) {
            return;
        }

        final String foodName = food.getName();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Common.loadImage(this, binding.foodInfoImage, FoodInfo.getFoodImage(foodName));
            binding.foodInfoImage.setVisibility(View.VISIBLE);
        } else {
            binding.foodInfoImage.setVisibility(View.GONE);
        }

        if (Common.isSupplement(food)) {
            binding.servingSizesCard.setVisibility(View.GONE);
            binding.foodTypesCard.setVisibility(View.GONE);
            return;
        }

        binding.changeUnitsButton.setOnClickListener(v -> {
            Prefs.getInstance(v.getContext()).toggleUnitType();
            initServingTypes(food);
        });
        initServingTypes(food);
        initFoodTypes(foodName);

        if (Common.EXERCISE.equalsIgnoreCase(food.getIdName())) {
            binding.changeUnitsContainer.setVisibility(View.GONE);
        }
    }

    private void initServingTypes(final Food food) {
        final List<String> servingSizes = FoodInfo.getServingSizes(food.getIdName(),
                Prefs.getInstance(this).getUnitTypePref());

        binding.changeUnitsButton.setText(Prefs.getInstance(this).getUnitTypePref() == Units.IMPERIAL ?
                R.string.imperial : R.string.metric);
        binding.foodServingSizes.setAdapter(new FoodServingsAdapter(servingSizes));
        binding.foodServingSizes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initFoodTypes(String foodName) {
        final List<String> foods = FoodInfo.getTypesOfFood(foodName);
        final List<String> videos = FoodInfo.getFoodVideosLink(foodName);

        binding.foodTypes.setAdapter(new FoodTypeAdapter(foods, videos));
        binding.foodTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void openFoodVideosInBrowser() {
        final Food food = getFood();
        if (food != null && !TextUtils.isEmpty(food.getName())) {
            Common.openUrlInExternalBrowser(this, FoodInfo.getFoodTypeVideosLink(food.getName()));
        }
    }

    private void displayTweakInfo() {
        binding.tweakInfoSection.setVisibility(View.VISIBLE);
        binding.foodInfoSection.setVisibility(View.GONE);

        setTitle(R.string.about_tweak);

        final Tweak tweak = getTweak();
        if (tweak == null || TextUtils.isEmpty(tweak.getName())) {
            return;
        }

        final String tweakName = tweak.getName();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Common.loadImage(this, binding.tweakInfoImage, FoodInfo.getTweakImage(tweakName));
            binding.tweakInfoImage.setVisibility(View.VISIBLE);
        } else {
            binding.tweakInfoImage.setVisibility(View.GONE);
        }

        binding.tweakShort.setText(FoodInfo.getTweakShort(tweakName));
        binding.tweakText.setText(FoodInfo.getTweakText(tweakName));
    }

    private void displayHistory() {
        if (getFood() != null) {
            isFoodHistory = true;
            initCalendar(getFood().getId(), getFood().getRecommendedAmount());
            displayEntriesForVisibleMonths(Calendar.getInstance(), getFood().getId());
        } else if (getTweak() != null) {
            isFoodHistory = false;
            initCalendar(getTweak().getId(), getTweak().getRecommendedAmount());
            displayEntriesForVisibleMonths(Calendar.getInstance(), getTweak().getId());
        }
    }

    private void initCalendar(final long id, final int recommendedServings) {
        fullServingsDates = new ArrayList<>();
        partialServingsDates = new ArrayList<>();

        binding.calendarView.setOnDateChangedListener((widget, date, selected) -> {
            setResult(Args.SELECTABLE_DATE_REQUEST, Common.createShowDateIntent(date.getYear(), date.getMonth(), date.getDay()));
            finish();
        });

        binding.calendarView.setOnMonthChangedListener((widget, date) -> displayEntriesForVisibleMonths(DateUtil.getCalendarForYearAndMonth(date.getYear(), date.getMonth()), id));

        binding.calendarLegend.setVisibility(recommendedServings > 1 ? View.VISIBLE : View.GONE);
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final long id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ColorDrawable bgLessThanRecServings = new ColorDrawable(
                    ContextCompat.getColor(InfoActivity.this, R.color.legend_less_than_recommended_servings));

            ColorDrawable bgRecServings = new ColorDrawable(
                    ContextCompat.getColor(InfoActivity.this, R.color.legend_recommended_servings));

            // We start 2 months in the past because this prevents "flickering" of dates when the user swipes to
            // the previous month. For instance, starting in February and swiping to January, the dates from
            // December that are shown in the January calendar will have their backgrounds noticeably flicker on.
            DateUtil.subtractTwoMonths(cal);

            int i = 0;
            do {
                final String monthStr = DateUtil.toStringYYYYMM(cal);

                if (!loadedMonths.contains(monthStr)) {
                    Map<Day, Boolean> servings;

                    if (isFoodHistory) {
                        servings = DDServings.getServingsOfFoodInYearAndMonth(id, DateUtil.getYear(cal), DateUtil.getMonthOneBased(cal));
                    } else {
                        servings = TweakServings.getServingsOfTweakInYearAndMonth(id, DateUtil.getYear(cal), DateUtil.getMonthOneBased(cal));
                    }

                    loadedMonths.add(monthStr);

                    for (Map.Entry<Day, Boolean> serving : servings.entrySet()) {
                        if (serving.getValue()) {
                            fullServingsDates.add(serving.getKey().getDate());
                        } else {
                            partialServingsDates.add(serving.getKey().getDate());
                        }
                    }
                }

                DateUtil.addOneMonth(cal);
                i++;
            } while (i < 3);

            handler.post(() -> {
                ArrayList<DayViewDecorator> decorators = new ArrayList<>();
                decorators.add(new CalendarHistoryDecorator(fullServingsDates, bgRecServings));
                decorators.add(new CalendarHistoryDecorator(partialServingsDates, bgLessThanRecServings));
                binding.calendarView.addDecorators(decorators);
            });
        });
    }
}
