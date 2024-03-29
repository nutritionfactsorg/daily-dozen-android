package org.nutritionfacts.dailydozen.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.ActivityHistoryBinding;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.util.CalendarHistoryDecorator;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hirondelle.date4j.DateTime;

public class HistoryActivity extends InfoActivity {
    private ActivityHistoryBinding binding;

    private boolean isFoodHistory = false;

    private final Set<String> loadedMonths = new HashSet<>();
    private List<DateTime> fullServingsDates;
    private List<DateTime> partialServingsDates;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fullServingsDates = new ArrayList<>();
        partialServingsDates = new ArrayList<>();
        if (savedInstanceState != null) {
            fullServingsDates = (ArrayList<DateTime>) savedInstanceState.getSerializable(Args.DATES_WITH_FULL_SERVINGS);
            partialServingsDates = (ArrayList<DateTime>) savedInstanceState.getSerializable(Args.DATES_WITH_PARTIAL_SERVINGS);
        }

        displayHistory();
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
                    ContextCompat.getColor(HistoryActivity.this, R.color.legend_less_than_recommended_servings));

            ColorDrawable bgRecServings = new ColorDrawable(
                    ContextCompat.getColor(HistoryActivity.this, R.color.legend_recommended_servings));

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
                            fullServingsDates.add(serving.getKey().getDateTime());
                        } else {
                            partialServingsDates.add(serving.getKey().getDateTime());
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
