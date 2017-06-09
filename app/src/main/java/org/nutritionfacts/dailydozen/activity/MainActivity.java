package org.nutritionfacts.dailydozen.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.BuildConfig;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.DatePagerAdapter;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.PermissionController;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.event.BackupCompleteEvent;
import org.nutritionfacts.dailydozen.event.CalculateStreaksTaskCompleteEvent;
import org.nutritionfacts.dailydozen.event.DisplayDateEvent;
import org.nutritionfacts.dailydozen.event.RestoreCompleteEvent;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.task.BackupTask;
import org.nutritionfacts.dailydozen.task.CalculateStreaksTask;
import org.nutritionfacts.dailydozen.task.RestoreTask;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {
    private static final String ALREADY_HANDLED_RESTORE_INTENT = "already_handled_restore_intent";

    private static final int DEBUG_SETTINGS_REQUEST = 1;
    private static final int FOOD_HISTORY_REQUEST = 2;

    @BindView(R.id.date_pager)
    protected ViewPager datePager;
    @BindView(R.id.date_pager_indicator)
    protected PagerTabStrip datePagerIndicator;

    private Handler dayChangeHandler;
    private Runnable dayChangeRunnable;

    private int daysSinceEpoch;

    private boolean alreadyHandledRestoreIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initDatePager();
        initDatePagerIndicator();

        calculateStreaksAfterDatabaseUpgradeToV2();

        handleIntentIfNecessary();
    }

    private void handleIntentIfNecessary() {
        final Intent intent = getIntent();

        if (intent != null) {
            final Bundle extras = intent.getExtras();

            if (extras != null) {
                if (extras.getBoolean(Args.OPEN_NOTIFICATION_SETTINGS, false)) {
                    NotificationUtil.dismissUpdateReminderNotification(this);
                    startActivity(new Intent(this, DailyReminderSettingsActivity.class));
                }
            }
        }
    }

    private void calculateStreaksAfterDatabaseUpgradeToV2() {
        if (!Prefs.getInstance(this).streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2()) {
            if (Servings.isEmpty()) {
                Prefs.getInstance(this).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
            } else {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_streaks_title)
                        .setMessage(R.string.dialog_streaks_message)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CalculateStreaksTask(MainActivity.this).execute();
                            }
                        })
                        .create().show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);

        // If the app is sent to the background and brought back to the foreground the next day, a crash results when
        // the adapter is found to return a different value from getCount() without notifyDataSetChanged() having been
        // called first. This is an attempt to fix that, but I am not sure that it works.
        // This bug was found by entering some data before bed and then bringing the app back to the foreground in the
        // morning to enter data. The app crashed immediately.
        // Solutions tried: datePagerAdapter.notifyDataSetChanged() did not work
        if (daysSinceEpoch < Day.getNumDaysSinceEpoch()) {
            initDatePager();
        }

        startDayChangeHandler();

        checkIfOpenedForRestore(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.unregister(this);

        stopDayChangeHandler();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_HANDLED_RESTORE_INTENT, alreadyHandledRestoreIntent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        alreadyHandledRestoreIntent = savedInstanceState.getBoolean(ALREADY_HANDLED_RESTORE_INTENT);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        alreadyHandledRestoreIntent = false;

        checkIfOpenedForRestore(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Only show the debug menu option if the apk is a debug build
        menu.findItem(R.id.menu_debug).setVisible(BuildConfig.DEBUG);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_latest_videos:
                Common.openUrlInExternalBrowser(this, R.string.url_latest_videos);
                return true;
            case R.id.menu_how_not_to_die:
                Common.openUrlInExternalBrowser(this, R.string.url_book);
                return true;
            case R.id.menu_donate:
                Common.openUrlInExternalBrowser(this, R.string.url_donate);
                return true;
            case R.id.menu_subscribe:
                Common.openUrlInExternalBrowser(this, R.string.url_subscribe);
                return true;
            case R.id.menu_open_source:
                Common.openUrlInExternalBrowser(this, R.string.url_open_source);
                return true;
            case R.id.menu_daily_reminder_settings:
                startActivity(new Intent(this, DailyReminderSettingsActivity.class));
                return true;
            case R.id.menu_backup:
                backup();
                return true;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_debug:
                startActivityForResult(new Intent(this, DebugActivity.class), DEBUG_SETTINGS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case DEBUG_SETTINGS_REQUEST:
                // Always refresh the data shown when returning from the Debug Activity
                initDatePager();
                break;
            case FOOD_HISTORY_REQUEST:
                if (data != null && data.hasExtra("date"))
                    datePager.setCurrentItem(Day.getNumDaysSinceEpoch(new DateTime(data.getStringExtra("date"))) - 1);
                break;
        }
    }

    private void initDatePager() {
        final DatePagerAdapter datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        datePager.setAdapter(datePagerAdapter);

        daysSinceEpoch = datePagerAdapter.getCount();

        // Go to today's date by default
        datePager.setCurrentItem(datePagerAdapter.getCount(), false);
    }

    private void initDatePagerIndicator() {
        datePagerIndicator.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        datePagerIndicator.setBackgroundResource(R.color.colorPrimary);
        datePagerIndicator.setTabIndicatorColorResource(R.color.colorAccent);
        datePagerIndicator.setDrawFullUnderline(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionController.grantedWriteExternalStorage(requestCode, grantResults)) {
            backup();
        } else {
            Common.showToast(this, R.string.permission_needed_to_write_storage);
        }
    }

    private void backup() {
        if (!Servings.isEmpty()) {
            if (PermissionController.canWriteExternalStorage(this)) {
                new BackupTask(this).execute(getBackupFile());
            } else {
                PermissionController.askForWriteExternalStorage(this);
            }
        } else {
            Common.showToast(this, R.string.no_servings_recorded);
        }
    }

    private void checkIfOpenedForRestore(final Intent intent) {
        if (intent == null || alreadyHandledRestoreIntent) {
            return;
        }

        final Uri restoreFileUri = intent.getData();

        if (restoreFileUri != null) {
            // FIXME: 2/20/16 this should only be set to true if the RestoreTask returns true (did not fail and was not cancelled)
            alreadyHandledRestoreIntent = true;

            if (!Servings.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.restore_confirm_title)
                        .setMessage(R.string.restore_confirm_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restore(restoreFileUri);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                restore(restoreFileUri);
            }
        }
    }

    private void restore(final Uri restoreFileUri) {
        new RestoreTask(this).execute(restoreFileUri);
    }

    public File getBackupFile() {
        return new File(getFilesDir(), "dailydozen_backup.csv");
    }

    private void shareBackupFile() {
        final File backupFile = getBackupFile();
        final String backupInstructions = TextUtils.join(Common.getLineSeparator(),
                getResources().getStringArray(R.array.backup_instructions_lines));
        final Uri backupFileUri = FileProvider.getUriForFile(this, Common.FILE_PROVIDER_AUTHORITY, backupFile);

        try {
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, backupFile.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, backupInstructions);
            shareIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
            shareIntent.setType(getString(R.string.backup_mimetype));
            startActivity(shareIntent);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_no_email_apps_title)
                    .setMessage(R.string.dialog_no_email_apps_message)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    @Subscribe
    public void onEvent(BackupCompleteEvent event) {
        if (event.isSuccess()) {
            shareBackupFile();
        }
    }

    @Subscribe
    public void onEvent(RestoreCompleteEvent event) {
        Common.showToast(this, event.isSuccess() ? R.string.restore_success : R.string.restore_failed);

        if (event.isSuccess()) {
            initDatePager();
        }
    }

    @Subscribe
    public void onEvent(CalculateStreaksTaskCompleteEvent event) {
        if (event.isSuccess()) {
            Prefs.getInstance(this).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
            initDatePager();
        }
    }

    private void startDayChangeHandler() {
        if (dayChangeRunnable == null) {
            dayChangeRunnable = new Runnable() {
                @Override
                public void run() {
                    initDatePager();
                    startDayChangeHandler();
                }
            };
        }

        stopDayChangeHandler();

        dayChangeHandler = new Handler();
        dayChangeHandler.postDelayed(dayChangeRunnable, Day.getMillisUntilMidnight());
    }

    private void stopDayChangeHandler() {
        if (dayChangeHandler != null && dayChangeRunnable != null) {
            dayChangeHandler.removeCallbacks(dayChangeRunnable);
            dayChangeHandler = null;
        }
    }

    @Subscribe
    public void onEvent(DisplayDateEvent event) {
        datePager.setCurrentItem(Day.getNumDaysSinceEpoch(event.getDate()));
    }

    public void openFoodHistory(final Context context, final Food food, final Day day){
        startActivityForResult(Common.createFoodHistoryIntent(context, food), FOOD_HISTORY_REQUEST);
    }
}