package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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

    private DatePagerAdapter datePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ensureAllFoodsExistInDatabase();
        ensureTodayExistsInDatabase();

        initDatePager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_to_today:
                goToToday();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDatePager() {
        datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        datePager.setAdapter(datePagerAdapter);
        goToToday();
    }

    private void goToToday() {
        if (datePagerAdapter != null) {
            final int indexOfLatestDate = datePagerAdapter.getIndexOfLastPage();
            datePager.setCurrentItem(indexOfLatestDate);
            datePageIndicator.setViewPager(datePager, indexOfLatestDate);
        }
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
