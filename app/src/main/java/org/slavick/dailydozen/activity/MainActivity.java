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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        datePager.setAdapter(new DatePagerAdapter(getSupportFragmentManager()));
    }
}
