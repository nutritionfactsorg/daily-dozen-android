package org.nutritionfacts.dailydozen.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.util.CalendarHistoryDecorator;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class TweakHistoryActivity extends TweakLoadingActivity {
    @BindView(R.id.history_card_container)
    protected CardView cardView;
    @BindView(R.id.calendar_legend)
    protected ViewGroup vgLegend;
    @BindView(R.id.calendarView)
    protected MaterialCalendarView calendarView;

    private Set<String> loadedMonths = new HashSet<>();
    private List<DateTime> fullServingsDates;
    private List<DateTime> partialServingsDates;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        fullServingsDates = new ArrayList<>();
        partialServingsDates = new ArrayList<>();
        if (savedInstanceState != null) {
            fullServingsDates = (ArrayList<DateTime>) savedInstanceState.getSerializable(Args.DATES_WITH_FULL_SERVINGS);
            partialServingsDates = (ArrayList<DateTime>) savedInstanceState.getSerializable(Args.DATES_WITH_PARTIAL_SERVINGS);
        }

        displayTweakHistory();
    }

    private void displayTweakHistory() {
        final Tweak tweak = getTweak();
        if (tweak != null) {
            initCalendar(tweak.getId(), tweak.getRecommendedAmount());

            displayEntriesForVisibleMonths(Calendar.getInstance(), tweak.getId());

            if (Common.isAppInDarkMode(this)) {
                cardView.setForceDarkAllowed(false);
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            }
        }
    }

    private void initCalendar(final long tweakId, final int recommendedServings) {
        fullServingsDates = new ArrayList<>();
        partialServingsDates = new ArrayList<>();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                setResult(Args.SELECTABLE_DATE_REQUEST, Common.createShowDateIntent(DateUtil.getCalendarForYearMonthAndDay(date.getYear(), date.getMonth(), date.getDay()).getTime()));
                finish();
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                displayEntriesForVisibleMonths(DateUtil.getCalendarForYearAndMonth(date.getYear(), date.getMonth()), tweakId);
            }
        });

        vgLegend.setVisibility(recommendedServings > 1 ? View.VISIBLE : View.GONE);
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final long tweakId) {
        new AsyncTask<Void, Void, Void>() {
            ColorDrawable bgLessThanRecServings;
            ColorDrawable bgRecServings;

            @Override
            protected Void doInBackground(Void... params) {
                bgLessThanRecServings = new ColorDrawable(
                        ContextCompat.getColor(TweakHistoryActivity.this, R.color.legend_less_than_recommended_servings));

                bgRecServings = new ColorDrawable(
                        ContextCompat.getColor(TweakHistoryActivity.this, R.color.legend_recommended_servings));

                // We start 2 months in the past because this prevents "flickering" of dates when the user swipes to
                // the previous month. For instance, starting in February and swiping to January, the dates from
                // December that are shown in the January calendar will have their backgrounds noticeably flicker on.
                DateUtil.subtractTwoMonths(cal);

                int i = 0;
                do {
                    final String monthStr = DateUtil.toStringYYYYMM(cal);

                    if (!loadedMonths.contains(monthStr)) {
                        final Map<Day, Boolean> servings = TweakServings.getServingsOfTweakInYearAndMonth(tweakId,
                                DateUtil.getYear(cal), DateUtil.getMonthOneBased(cal));

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

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                ArrayList<DayViewDecorator> decorators = new ArrayList<>();
                decorators.add(new CalendarHistoryDecorator(fullServingsDates, bgRecServings));
                decorators.add(new CalendarHistoryDecorator(partialServingsDates, bgLessThanRecServings));
                calendarView.addDecorators(decorators);
            }
        }.execute();
    }
}
