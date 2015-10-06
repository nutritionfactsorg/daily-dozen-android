package org.nutritionfacts.dailydozen.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import org.nutritionfacts.dailydozen.fragment.WelcomeFragment;
import org.nutritionfacts.dailydozen.rowItem.FoodTypeRowItem;
import org.nutritionfacts.dailydozen.rowItem.InvisibleHeaderRowItem;
import org.nutritionfacts.dailydozen.user.UserManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        QxRecyclerViewAdapter.OnRecyclerViewRowItemClickedListener, FoodTypeDetailFragment.OnConsumedServingChangedListener {

    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private boolean showWelcomeScreen;

    private DBDailyReport dailyReport;

    private ProgressBar progressBar;
    private TextView progressTextView;

    protected QxRecyclerView listView;
    protected QxRecyclerViewAdapter adapter;
    private List<FoodTypeRowItem> rowItems;

    private Date cachedDate;

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

//            UserManager.getInstance().createUser();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onResume() {
        super.onResume();

        Date date = DataManager.getInstance().getTodaysDate();

        boolean forceRebuildData = false;
        if (cachedDate == null || cachedDate.compareTo(date) != 0) {
            cachedDate = date;
            forceRebuildData = true;
        }

        if (!adapter.getHasBeenInitialized() || forceRebuildData) {
            buildFoodList();
        }
        updateProgressBar();

        if (showWelcomeScreen) {
            showWelcomeScreen = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    WelcomeFragment fragment = WelcomeFragment.newInstance();
                    fragment.show(getSupportFragmentManager(), "welcome_fragment");
                }
            }, 400);
        }
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
                        mDrawerLayout.closeDrawers();

                        Intent browserIntent = null;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_video: {

                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nutritionfacts.org/"));

                                break;
                            }
                            case R.id.nav_donate: {

                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nutritionfacts.org/donate/"));
                                break;
                            }
                            case R.id.nav_book: {

                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nutritionfacts.org/book"));
                                break;
                            }
                            case R.id.nav_subscribe: {

                                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nutritionfacts.org/subscribe/"));
                                break;
                            }
                            case R.id.nav_acknowledgements: {

                                SpannableString spanned = new SpannableString(getString(R.string.acknowledgements_body));
                                Linkify.addLinks(spanned, Linkify.ALL);

                                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.acknowledgements_title)
                                        .setMessage(spanned)
                                        .setPositiveButton(R.string.dismiss, null)
                                        .create();

                                dialog.show();

                                View textView = dialog.findViewById(android.R.id.message);

                                if (textView != null && textView instanceof TextView) {
                                    ((TextView)textView).setMovementMethod(LinkMovementMethod.getInstance());
                                }

                                return true;
                            }
                        }

                        if (browserIntent != null) {
                            startActivity(browserIntent);
                        }

                        /*CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());

                        String url = "https://paul.kinlan.me/";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                        Bundle extras = new Bundle;
                        extras.putBinder(EXTRA_CUSTOM_TABS_SESSION,
                                sessionICustomTabsCallback.asBinder() /* Set to null for no session */
                        /*);
                        intent.putExtras(extras);
*/
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
