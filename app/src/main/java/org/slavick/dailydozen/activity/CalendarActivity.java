package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Servings;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {
    private CaldroidFragment calendar;

    private long foodId;
    private String foodName;

    private HashMap<Date, Integer> datesWithEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        datesWithEvents = new HashMap<>();
        if (savedInstanceState != null) {
            datesWithEvents = (HashMap<Date, Integer>) savedInstanceState.getSerializable(Args.DATES_WITH_EVENTS);
        }

        displayCalendarForFood();
    }

    private void displayCalendarForFood() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Args.FOOD_ID) && intent.hasExtra(Args.FOOD_NAME)) {
            foodId = intent.getLongExtra(Args.FOOD_ID, 0);
            foodName = intent.getStringExtra(Args.FOOD_NAME);

            initCalendar();
        }
    }

    private void initCalendar() {
        final Calendar cal = Calendar.getInstance();
        calendar = new CaldroidFragment();

        final Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));

        calendar.setArguments(args);

        datesWithEvents = new HashMap<>();
        displayEntriesForVisibleMonths(cal);

        calendar.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);

                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, 1); // The month property of Calendar starts at 0

                displayEntriesForVisibleMonths(cal);
            }
        });

        final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_fragment_container, calendar);
        t.commit();
    }

    private void displayEntriesForVisibleMonths(final Calendar cal) {
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
                    for (Servings servings : Servings.getServingsOfFoodInMonth(foodId, cal)) {
                        datesWithEvents.put(servings.getDate().getDateObject(), R.color.caldroid_sky_blue);
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
