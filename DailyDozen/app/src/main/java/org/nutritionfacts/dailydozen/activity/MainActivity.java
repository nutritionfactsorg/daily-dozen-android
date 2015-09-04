package org.nutritionfacts.dailydozen.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qxmd.qxrecyclerview.QxRecyclerView;
import com.qxmd.qxrecyclerview.QxRecyclerViewAdapter;
import com.qxmd.qxrecyclerview.QxRecyclerViewRowItem;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.data.DataManager;
import org.nutritionfacts.dailydozen.db.DBConsumption;
import org.nutritionfacts.dailydozen.db.DBDailyReport;
import org.nutritionfacts.dailydozen.fragment.FoodTypeDetailFragment;
import org.nutritionfacts.dailydozen.rowItem.FoodTypeRowItem;
import org.nutritionfacts.dailydozen.rowItem.InvisibleHeaderRowItem;
import org.nutritionfacts.dailydozen.user.UserManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        QxRecyclerViewAdapter.OnRecyclerViewRowItemClickedListener, FoodTypeDetailFragment.OnConsumedServingChangedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private boolean showWelcomeScreen;

    private DBDailyReport dailyReport;

    private ProgressBar progressBar;
    private TextView progressTextView;

    protected QxRecyclerView listView;
    protected QxRecyclerViewAdapter adapter;
    private List<FoodTypeRowItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name_full);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.content_desc_drawer_open,
                R.string.content_desc_drawer_close) {
            @Override
            public void onDrawerClosed(View view) {

            }

            @Override
            public void onDrawerOpened(View view) {

            }
        };

        mDrawerLayout.setDrawerListener(drawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (!UserManager.getInstance().hasUserRegistered()) {
            showWelcomeScreen = true;

            UserManager.getInstance().createUser();
        }
        // else, UserManager DBUser will be initialized in DailyDozenApplication

        dailyReport = DataManager.getInstance().getReportForToday();

        adapter = new QxRecyclerViewAdapter();
        adapter.setHasStableIds(false);
        adapter.setOnClickListener(this);

        listView = (QxRecyclerView) findViewById(R.id.recycler_view);
        listView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressTextView = (TextView) findViewById(R.id.percent_progress_text_view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!adapter.getHasBeenInitialized()) {
            buildFoodList();
        }
        updateProgressBar();
    }

    private void buildFoodList() {

        adapter.reset();

        List<DBConsumption> consumptions = dailyReport.getConsumptions();

        rowItems = new ArrayList<>(consumptions.size());

        for (DBConsumption consumption : consumptions) {
            rowItems.add(new FoodTypeRowItem(consumption, this));
        }

        adapter.addSectionWithHeaderItem(new InvisibleHeaderRowItem(), rowItems);

        adapter.notifyDataSetChanged();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onRecyclerViewRowItemClicked(QxRecyclerViewRowItem rowItem, QxRecyclerViewAdapter adapter, View view, int position) {

        if (rowItem instanceof FoodTypeRowItem) {
            DBConsumption consumption = ((FoodTypeRowItem) rowItem).consumption;

            FoodTypeDetailFragment fragment = FoodTypeDetailFragment.newInstance(consumption.getId());
            fragment.show(getSupportFragmentManager(), "dialog_fragment");
        }
    }

    @Override
    public void onConsumedServingChanged(long consumptionId) {

        if (rowItems == null) {
            return;
        }

        DBConsumption consumption = null;
        FoodTypeRowItem changedRowItem = null;

        for (FoodTypeRowItem rowItem : rowItems) {
            if (rowItem.consumption.getId().equals(consumptionId)) {

                changedRowItem = rowItem;
                break;
            }
        }

        if (changedRowItem != null) {
            int position = adapter.getPositionForRowItem(changedRowItem);

            if (position >= 0) {
                adapter.notifyItemChanged(position);
                updateProgressBar();
            }
        }
    }

    private void updateProgressBar() {

        double totalServings = 0.0;
        double consumedServings = 0.0;

        List<DBConsumption> consumptions = dailyReport.getConsumptions();

        for (DBConsumption consumption : consumptions) {

            if (consumption.foodType.recommendedServingCount != null &&
                    consumption.foodType.recommendedServingCount >= 0.0) {
                totalServings += consumption.foodType.recommendedServingCount;

                if (consumption.getConsumedServingCount() != null) {
                    consumedServings += Math.min(consumption.getConsumedServingCount(), consumption.foodType.recommendedServingCount);
                }
            }
        }

        int percentProgress = (int)Math.round(consumedServings/totalServings*100);
        progressBar.setProgress(percentProgress);
        progressTextView.setText(percentProgress + " %");
    }
}
