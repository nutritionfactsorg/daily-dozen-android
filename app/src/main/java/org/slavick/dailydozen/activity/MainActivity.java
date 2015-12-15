package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.DatePagerAdapter;
import org.slavick.dailydozen.model.Date;
import org.slavick.dailydozen.model.Food;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.date_pager)
    protected ViewPager datePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ensureAllFoodsExistInDatabase();
        ensureTodayExistsInDatabase();

        initDatePager();
    }

    private void initDatePager() {
        datePager.setAdapter(new DatePagerAdapter(getSupportFragmentManager()));
    }

    private void ensureAllFoodsExistInDatabase() {
        Food.ensureAllFoodsExistInDatabase(
                getResources().getStringArray(R.array.food_names),
                getResources().getIntArray(R.array.food_quantities));
    }

    private void ensureTodayExistsInDatabase() {
        Date.createToday();
    }
}
