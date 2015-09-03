package org.nutritionfacts.dailydozen.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.qxmd.qxrecyclerview.QxRecyclerView;
import com.qxmd.qxrecyclerview.QxRecyclerViewAdapter;
import com.qxmd.qxrecyclerview.QxRecyclerViewRowItem;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.data.DataManager;
import org.nutritionfacts.dailydozen.db.DBConsumption;
import org.nutritionfacts.dailydozen.db.DBDailyReport;
import org.nutritionfacts.dailydozen.fragment.FoodTypeDetailFragment;
import org.nutritionfacts.dailydozen.rowItem.FoodTypeRowItem;
import org.nutritionfacts.dailydozen.user.UserManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        QxRecyclerViewAdapter.OnRecyclerViewRowItemClickedListener, FoodTypeDetailFragment.OnConsumedServingChangedListener {

    private DrawerLayout mDrawerLayout;

    private boolean showWelcomeScreen;

    private DBDailyReport dailyReport;

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!adapter.getHasBeenInitialized()) {
            buildFoodList();
        }
    }

    private void buildFoodList() {

        adapter.reset();

        List<DBConsumption> consumptions = dailyReport.getConsumptions();

        rowItems = new ArrayList<>(consumptions.size());

        for (DBConsumption consumption : consumptions) {
            rowItems.add(new FoodTypeRowItem(consumption, this));
        }

        adapter.addSectionWithTitle("Test", rowItems);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            }
        }
    }
}
