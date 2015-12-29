package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.FoodInfo;
import org.slavick.dailydozen.model.Servings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodInfoActivity extends AppCompatActivity {
    @Bind(R.id.food_types)
    protected ListView lvFoodTypes;

    @Bind(R.id.food_serving_sizes)
    protected ListView lvFoodServingSizes;

    private CaldroidFragment calendar;

    private HashMap<Date, Integer> datesWithEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        ButterKnife.bind(this);

        datesWithEvents = new HashMap<>();
        if (savedInstanceState != null) {
            datesWithEvents = (HashMap<Date, Integer>) savedInstanceState.getSerializable(Args.DATES_WITH_EVENTS);
        }

        displayInfoForFood();
    }

    private void displayInfoForFood() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Args.FOOD)) {
            final Food food = (Food) intent.getSerializableExtra(Args.FOOD);

            final String foodName = food.getName();

            setTitle(foodName);

            initCalendar(food);
            initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
            initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
        }
    }

    private void initCalendar(final Food food) {
        final Calendar cal = Calendar.getInstance();
        calendar = new CaldroidFragment();

        final Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

        calendar.setArguments(args);

        datesWithEvents = new HashMap<>();
        displayEntriesForVisibleMonths(cal, food);

        calendar.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);

                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, 1); // The month property of Calendar starts at 0

                displayEntriesForVisibleMonths(cal, food);
            }
        });

        final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_fragment_container, calendar);
        t.commit();
    }

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    public void fullyExpandList(final ListView list) {
        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getListViewHeight(list)));
    }

    private int getListViewHeight(final ListView list) {
        final Adapter adapter = list.getAdapter();
        final int count = adapter.getCount();

        list.measure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // The (count - 1) hides the final list item divider
        return list.getMeasuredHeight() * count + ((count - 1) * list.getDividerHeight());
    }

    private void displayEntriesForVisibleMonths(final Calendar cal, final Food food) {
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
                    final Map<Date, Boolean> servings = Servings.getServingsOfFoodInMonth(food.getFoodId(), cal);

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
