package org.nutritionfacts.dailydozen.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.BuildConfig;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.DailyDozenPagerAdapter;
import org.nutritionfacts.dailydozen.adapter.TweaksPagerAdapter;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.PermissionController;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.ActivityMainBinding;
import org.nutritionfacts.dailydozen.event.BackupCompleteEvent;
import org.nutritionfacts.dailydozen.event.CalculateStreaksTaskCompleteEvent;
import org.nutritionfacts.dailydozen.event.DisplayDateEvent;
import org.nutritionfacts.dailydozen.event.RestoreCompleteEvent;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.task.BackupTask;
import org.nutritionfacts.dailydozen.task.CalculateStreaksTask;
import org.nutritionfacts.dailydozen.task.ProgressListener;
import org.nutritionfacts.dailydozen.task.RestoreTask;
import org.nutritionfacts.dailydozen.task.TaskRunner;
import org.nutritionfacts.dailydozen.util.DateUtil;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import java.io.File;
import java.util.Date;

import hirondelle.date4j.DateTime;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ProgressListener {
    private static final String ALREADY_HANDLED_RESTORE_INTENT = "already_handled_restore_intent";

    private ActivityMainBinding binding;

    private ProgressDialog progressDialog;

    private MenuItem menuToggleModes;

    private Handler dayChangeHandler;
    private Runnable dayChangeRunnable;

    private int daysSinceEpoch;

    private boolean alreadyHandledRestoreIntent;

    private boolean inDailyDozenMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                    startActivity(new Intent(this, DailyReminderSettingsActivity.class));
                }
            }
        }
    }

    private void calculateStreaksAfterDatabaseUpgradeToV2() {
        if (!Prefs.getInstance(this).streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2()) {
            if (DDServings.isEmpty()) {
                Prefs.getInstance(this).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
            } else {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_streaks_title)
                        .setMessage(R.string.dialog_streaks_message)
                        .setPositiveButton(R.string.OK, (dialog, which) -> new CalculateStreaksTask(MainActivity.this).execute())
                        .create().show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);

        NotificationUtil.dismissUpdateReminderNotification(this);

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

        menuToggleModes = menu.findItem(R.id.menu_toggle_modes);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toggleTweaksMenuItemVisibility();
        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleTweaksMenuItemVisibility() {
        if (menuToggleModes != null) {
            menuToggleModes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_modes:
                inDailyDozenMode = !inDailyDozenMode;
                if (inDailyDozenMode) {
                    setTitle(R.string.app_name);
                    item.setTitle(R.string.twenty_one_tweaks);
                } else {
                    setTitle(R.string.twenty_one_tweaks);
                    item.setTitle(R.string.app_name);
                }
                initDatePager();
                return true;
            case R.id.menu_latest_videos:
                Common.openUrlInExternalBrowser(this, R.string.url_latest_videos);
                return true;
            case R.id.menu_how_not_to_die:
                Common.openUrlInExternalBrowser(this, R.string.url_how_not_to_die);
                return true;
            case R.id.menu_cookbook:
                Common.openUrlInExternalBrowser(this, R.string.url_cookbook);
                return true;
            case R.id.menu_how_not_to_diet:
                Common.openUrlInExternalBrowser(this, R.string.url_how_not_to_diet);
                return true;
            case R.id.menu_daily_dozen_challenge:
                Common.openUrlInExternalBrowser(this, R.string.url_daily_dozen_challenge);
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
                startActivityForResult(new Intent(this, DebugActivity.class), Args.DEBUG_SETTINGS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Args.DEBUG_SETTINGS_REQUEST:
                // Always refresh the data shown when returning from the Debug Activity
                initDatePager();
                break;
            case Args.SELECTABLE_DATE_REQUEST:
                if (data != null && data.hasExtra(Args.DATE)) {
                    setDatePagerDate(DateUtil.convertDateToDateTime((Date) data.getSerializableExtra(Args.DATE)));
                }
                break;
        }
    }

    private void initDatePager() {
        final FragmentStatePagerAdapter pagerAdapter;
        if (inDailyDozenMode) {
            pagerAdapter = new DailyDozenPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        } else {
            pagerAdapter = new TweaksPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        binding.datePager.setAdapter(pagerAdapter);
        daysSinceEpoch = pagerAdapter.getCount();

        // Go to today's date by default
        binding.datePager.setCurrentItem(pagerAdapter.getCount(), false);
    }

    private void initDatePagerIndicator() {
        binding.datePagerIndicator.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        binding.datePagerIndicator.setBackgroundResource(R.color.colorPrimary);
        binding.datePagerIndicator.setTabIndicatorColorResource(R.color.colorAccent);
        binding.datePagerIndicator.setDrawFullUnderline(false);
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
        if (!DDServings.isEmpty()) {
            if (PermissionController.canWriteExternalStorage(this)) {
                new TaskRunner().executeAsync(new BackupTask(this, getBackupFile()));
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

            if (!DDServings.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.restore_confirm_title)
                        .setMessage(R.string.restore_confirm_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            restore(restoreFileUri);
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
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
        return new File(getFilesDir(), "dailydozen_backup.json");
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
                    .setPositiveButton(R.string.OK, (dialog, which) -> dialog.dismiss())
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
            dayChangeRunnable = () -> {
                initDatePager();
                startDayChangeHandler();
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
        setDatePagerDate(event.getDate());
    }

    private void setDatePagerDate(final DateTime dateTime) {
        if (dateTime != null) {
            Timber.d("Changing displayed date to %s", dateTime.toString());
            binding.datePager.setCurrentItem(Day.getNumDaysSinceEpoch(dateTime));
        }
    }

    @Override
    public void showProgressBar(int titleId) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(titleId);
        progressDialog.show();
    }

    @Override
    public void updateProgressBar(int current, int total) {
        progressDialog.setProgress(current);
        progressDialog.setMax(total);
    }

    @Override
    public void hideProgressBar() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Timber.e("hideProgressBar: Exception while trying to dismiss progress dialog");
        } finally {
            progressDialog = null;
        }

    }
}