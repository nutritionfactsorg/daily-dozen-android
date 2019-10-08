package org.nutritionfacts.dailydozen.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class FoodHistoryActivity extends FoodLoadingActivity {
    @BindView(R.id.calendar_legend)
    protected ViewGroup vgLegend;

//    private CaldroidFragment caldroid;

    private Set<String> loadedMonths = new HashSet<>();
    private Map<DateTime, Drawable> datesWithEvents;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_history);
        ButterKnife.bind(this);

        datesWithEvents = new ArrayMap<>();
        if (savedInstanceState != null) {
            datesWithEvents = (ArrayMap<DateTime, Drawable>) savedInstanceState.getSerializable(Args.DATES_WITH_EVENTS);
        }

        displayFoodHistory();
    }

    private void displayFoodHistory() {
        final Food food = getFood();
        if (food != null) {
            initCalendar(food.getId(), food.getRecommendedServings());
        }
    }

    private void initCalendar(final long foodId, final int recommendedServings) {
        datesWithEvents = new ArrayMap<>();

//        caldroid = CaldroidFragment.newInstance("", DateUtil.getCurrentMonthOneBased(), DateUtil.getCurrentYear());
//
//        caldroid.setCaldroidListener(new CaldroidListener() {
//            @Override
//            public void onSelectDate(Date date, View view) {
//                setResult(Args.SELECTABLE_DATE_REQUEST, Common.createShowDateIntent(date));
//                finish();
//            }
//
//            @Override
//            public void onChangeMonth(int month, int year) {
//                super.onChangeMonth(month, year);
//                displayEntriesForVisibleMonths(DateUtil.getCalendarForYearAndMonth(year, month - 1), foodId);
//            }
//        });

        vgLegend.setVisibility(recommendedServings > 1 ? View.VISIBLE : View.GONE);

//        final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//        t.replace(R.id.calendar_fragment_container, caldroid);
//        t.commit();
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final long foodId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final ColorDrawable bgLessThanRecServings = new ColorDrawable(
                        ContextCompat.getColor(FoodHistoryActivity.this, R.color.legend_less_than_recommended_servings));

                final ColorDrawable bgRecServings = new ColorDrawable(
                        ContextCompat.getColor(FoodHistoryActivity.this, R.color.legend_recommended_servings));

                // We start 2 months in the past because this prevents "flickering" of dates when the user swipes to
                // the previous month. For instance, starting in February and swiping to January, the dates from
                // December that are shown in the January calendar will have their backgrounds noticeably flicker on.
                DateUtil.subtractTwoMonths(cal);

                int i = 0;
                do {
                    final String monthStr = DateUtil.toStringYYYYMM(cal);

                    if (!loadedMonths.contains(monthStr)) {
                        final Map<Day, Boolean> servings = Servings.getServingsOfFoodInYearAndMonth(foodId,
                                DateUtil.getYear(cal), DateUtil.getMonthOneBased(cal));

                        loadedMonths.add(monthStr);

                        for (Map.Entry<Day, Boolean> serving : servings.entrySet()) {
                            datesWithEvents.put(
                                    serving.getKey().getDateTime(),
                                    serving.getValue() ? bgRecServings : bgLessThanRecServings);
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

//                caldroid.setBackgroundDrawableForDateTimes(datesWithEvents);
//                caldroid.refreshView();
            }
        }.execute();
    }
}
