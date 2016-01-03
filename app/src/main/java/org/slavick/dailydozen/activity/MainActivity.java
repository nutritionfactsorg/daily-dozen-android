package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.DatePagerAdapter;

public class MainActivity extends AppCompatActivity {
    protected ViewPager datePager;

    private int daysSinceEpoch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePager = (ViewPager) findViewById(R.id.date_pager);

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
        // Solutions tried: datePagerAdapter.notifyDataSetChanged() did not work
        if (daysSinceEpoch < Common.getDaysSinceEpoch()) {
            initDatePager();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDatePager() {
        final DatePagerAdapter datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        datePager.setAdapter(datePagerAdapter);

        daysSinceEpoch = datePagerAdapter.getCount();

        // Go to today's date by default
        datePager.setCurrentItem(datePagerAdapter.getCount(), false);
    }
}
