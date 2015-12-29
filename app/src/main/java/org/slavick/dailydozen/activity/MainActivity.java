package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.DatePagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.date_pager)
    protected ViewPager datePager;

    private DatePagerAdapter datePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initDatePager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If the app is sent to the background and brought back to the foreground the next day, a crash results when
        // the adapter is found to return a different value from getCount() without notifyDataSetChanged() having been
        // called first. This is an attempt to fix that, but I am not sure that it works.
        // This bug was found by entering some data before bed and then bringing the app back to the foreground in the
        // morning to enter data. The app crashed immediately.
        datePagerAdapter.notifyDataSetChanged();
    }

    private void initDatePager() {
        datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        datePager.setAdapter(datePagerAdapter);

        // Go to today's date by default
        datePager.setCurrentItem(datePagerAdapter.getCount(), false);
    }
}
