package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.FoodInfo;
import org.slavick.dailydozen.model.Servings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodInfoActivity extends AppCompatActivity {
    protected ViewGroup vgLegend;
    protected ListView lvFoodTypes;
    protected ListView lvFoodServingSizes;

    private CaldroidFragment calendar;

    private HashMap<Date, Integer> datesWithEvents;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);

        vgLegend = (ViewGroup) findViewById(R.id.calendar_legend);
        lvFoodTypes = (ListView) findViewById(R.id.food_types);
        lvFoodServingSizes = (ListView) findViewById(R.id.food_serving_sizes);

        initActionBar();

        datesWithEvents = new HashMap<>();
        if (savedInstanceState != null) {
            datesWithEvents = (HashMap<Date, Integer>) savedInstanceState.getSerializable(Args.DATES_WITH_EVENTS);
        }

        displayInfoForFood();
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayInfoForFood() {
        final Intent intent = getIntent();
        if (intent != null) {
            final long foodId = intent.getLongExtra(Args.FOOD_ID, -1);
            final String foodName = intent.getStringExtra(Args.FOOD_NAME);
            final int recommendedServings = intent.getIntExtra(Args.FOOD_RECOMMENDED_SERVINGS, 1);

            setTitle(foodName);

            initCalendar(foodId, recommendedServings);
            initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
            initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
        }
    }

    private void initCalendar(final long foodId, final int recommendedServings) {
        final Calendar cal = Calendar.getInstance(Locale.getDefault());
        calendar = new CaldroidFragment();

        final Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

        calendar.setArguments(args);

        datesWithEvents = new HashMap<>();

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

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        Common.fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final long foodId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                int i = 0;
                cal.add(Calendar.MONTH, -2);
                do {
                    final Map<Date, Boolean> servings = Servings.getServingsOfFoodInMonth(foodId, cal);

                    for (Map.Entry<Date, Boolean> serving : servings.entrySet()) {
                        datesWithEvents.put(serving.getKey(), serving.getValue() ?
                                R.color.legend_recommended_servings : R.color.legend_less_than_recommended_servings);
                    }

                    cal.add(Calendar.MONTH, 1);
                    i++;
                } while (i < 3);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                calendar.setBackgroundResourceForDates(datesWithEvents);
                calendar.refreshView();
            }
        }.execute();
    }
}
