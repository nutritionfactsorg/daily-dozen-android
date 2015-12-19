package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Food;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    private CaldroidFragment calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        displayCalendarForFood();
    }

    private void displayCalendarForFood() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Args.FOOD_ARG)) {
            final Food food = (Food) intent.getSerializableExtra(Args.FOOD_ARG);

            initCalendar();
            displayDatesFoodEaten(food);
        }
    }

    private void initCalendar() {
        calendar = new CaldroidFragment();
        final Bundle args = new Bundle();
        final Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        calendar.setArguments(args);

        final FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_fragment_container, calendar);
        t.commit();
    }

    private void displayDatesFoodEaten(Food food) {
        if (food != null) {
//            food.getEatenOnDaysInMonth()

            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }
}
