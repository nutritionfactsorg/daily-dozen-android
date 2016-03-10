package org.slavick.dailydozen.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import hirondelle.date4j.DateTime;

public class FoodHistoryActivity extends FoodLoadingActivity {
    private static final String TAG = "FoodHistoryActivity";

    protected ViewGroup vgLegend;

    private CaldroidFragment calendar;

    private Map<DateTime, Drawable> datesWithEvents;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_history);

        vgLegend = (ViewGroup) findViewById(R.id.calendar_legend);

        datesWithEvents = new ArrayMap<>();
        if (savedInstanceState != null) {
            datesWithEvents = (ArrayMap<DateTime, Drawable>) savedInstanceState.getSerializable(Args.DATES_WITH_EVENTS);
        }

        displayFoodHistory();
    }

    private void displayFoodHistory() {
        final Food food = getFood();
        initCalendar(food.getId(), food.getRecommendedServings());
    }

    private void initCalendar(final long foodId, final int recommendedServings) {
        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        calendar = new CaldroidFragment();

        final Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

        calendar.setArguments(args);

        datesWithEvents = new ArrayMap<>();

        calendar.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);

                final Calendar cal = Calendar.getInstance(Locale.getDefault());
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1); // The month property of Calendar starts at 0

                displayEntriesForVisibleMonths(cal, foodId);
            }
        });

        vgLegend.setVisibility(recommendedServings > 1 ? View.VISIBLE : View.GONE);

        final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_fragment_container, calendar);
        t.commit();
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final long foodId) {
        new AsyncTask<Void, Void, Void>() {
            private Set<Calendar> alreadyLoadedMonths = new HashSet<>();

            @Override
            protected Void doInBackground(Void... params) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                final ColorDrawable bgLessThanRecServings = new ColorDrawable(
                        ContextCompat.getColor(FoodHistoryActivity.this, R.color.legend_less_than_recommended_servings));

                final ColorDrawable bgRecServings = new ColorDrawable(
                        ContextCompat.getColor(FoodHistoryActivity.this, R.color.legend_recommended_servings));

                // We start 2 months in the past because this prevents "flickering" of dates when the user swipes to
                // the previous month. For instance, starting in February and swiping to January, the dates from
                // December that are shown in the January calendar will have their backgrounds noticeably flicker on.
                cal.add(Calendar.MONTH, -2);

                int i = 0;
                do {
                    // TODO: test that this does not reload months
                    if (!alreadyLoadedMonths.contains(cal)) {
                        final Map<Day, Boolean> servings = Servings.getServingsOfFoodInMonth(foodId, cal);
                        alreadyLoadedMonths.add(cal);
                        Log.d(TAG, String.format("loading month: %s %s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1));

                        for (Map.Entry<Day, Boolean> serving : servings.entrySet()) {
                            datesWithEvents.put(
                                    serving.getKey().getDateTime(),
                                    serving.getValue() ? bgRecServings : bgLessThanRecServings);
                        }
                    } else {
                        Log.d(TAG, String.format("skipping month: %s %s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1));
                    }

                    cal.add(Calendar.MONTH, 1);
                    i++;
                } while (i < 3);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                calendar.setBackgroundDrawableForDateTimes(datesWithEvents);
                calendar.refreshView();
            }
        }.execute();
    }
}
