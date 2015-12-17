package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.viewpagerindicator.TitlePageIndicator;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.DatePagerAdapter;
import org.slavick.dailydozen.model.Date;
import org.slavick.dailydozen.model.Food;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.date_pager_indicator)
    protected TitlePageIndicator datePageIndicator;

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
        final DatePagerAdapter datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        final int indexOfLatestDate = datePagerAdapter.getIndexOfLastPage();

        datePager.setAdapter(datePagerAdapter);
        datePager.setCurrentItem(indexOfLatestDate);

        datePageIndicator.setViewPager(datePager, indexOfLatestDate);
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
