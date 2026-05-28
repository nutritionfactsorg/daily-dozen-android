package org.nutritionfacts.dailydozen.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.BuildConfig;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.DatePagerAdapter;
import org.nutritionfacts.dailydozen.controller.Bus;
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
import java.time.LocalDate;
import java.util.Date;

import timber.log.Timber;

public class MainActivity extends DailyDozenActivity implements ProgressListener, DateSelectionHost {
    private static final String ALREADY_HANDLED_RESTORE_INTENT = "already_handled_restore_intent";
    private static final String RESTORE_IN_PROGRESS = "restore_in_progress";
    private static final String RESTORE_CONFIRM_DIALOG_SHOWN = "restore_confirm_dialog_shown";

    private ActivityMainBinding binding;

    private MenuItem menuToggleModes;
    private MenuItem menuToggleNightMode;

    private int daysSinceEpoch;

    private boolean alreadyHandledRestoreIntent;
    private boolean restoreInProgress;
    private boolean restoreConfirmDialogShown;

    private boolean inDailyDozenMode = true;

    private ActivityResultLauncher<Intent> dateSelectionLauncher;
    private ActivityResultLauncher<Intent> debugSettingsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dateSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    final Intent data = result.getData();
                    if (data != null && data.hasExtra(Args.DATE)) {
                        final Date date = IntentCompat.getSerializableExtra(data, Args.DATE, Date.class);
                        setDatePagerDate(DateUtil.convertToLocalDate(date));
                    }
                });

        debugSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> initDatePager());

        if (savedInstanceState != null) {
            alreadyHandledRestoreIntent = savedInstanceState.getBoolean(ALREADY_HANDLED_RESTORE_INTENT);
            restoreInProgress = savedInstanceState.getBoolean(RESTORE_IN_PROGRESS);
            restoreConfirmDialogShown = savedInstanceState.getBoolean(RESTORE_CONFIRM_DIALOG_SHOWN);
        }

        initDatePager();

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
                        .setPositiveButton(R.string.OK, (dialog, which) -> new TaskRunner().executeAsync(new CalculateStreaksTask(this)))
                        .create().show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);

        NotificationUtil.dismissUpdateReminderNotification(this);

        // If the app is sent to the background and brought back to the foreground the next day,
        // this code will change to today's date.
        if (daysSinceEpoch < Day.getNumDaysSinceEpoch()) {
            initDatePager();
            Bus.displayLatestDate();
        }

        checkIfOpenedForRestore(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_HANDLED_RESTORE_INTENT, alreadyHandledRestoreIntent);
        outState.putBoolean(RESTORE_IN_PROGRESS, restoreInProgress);
        outState.putBoolean(RESTORE_CONFIRM_DIALOG_SHOWN, restoreConfirmDialogShown);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        alreadyHandledRestoreIntent = savedInstanceState.getBoolean(ALREADY_HANDLED_RESTORE_INTENT);
        restoreInProgress = savedInstanceState.getBoolean(RESTORE_IN_PROGRESS);
        restoreConfirmDialogShown = savedInstanceState.getBoolean(RESTORE_CONFIRM_DIALOG_SHOWN);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (isRestoreIntent(intent)) {
            alreadyHandledRestoreIntent = false;
            restoreInProgress = false;
            restoreConfirmDialogShown = false;
            checkIfOpenedForRestore(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        // Only show the debug menu option if the apk is a debug build
        menu.findItem(R.id.menu_debug).setVisible(BuildConfig.DEBUG);

        menuToggleModes = menu.findItem(R.id.menu_toggle_modes);
        menuToggleNightMode = menu.findItem(R.id.menu_toggle_night_mode);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toggleTweaksMenuItemVisibility();
        updateAppModeToggle();
        updateNightModeToggleIcon();
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateAppModeToggle() {
        if (inDailyDozenMode) {
            setTitle(R.string.app_name);
            menuToggleModes.setTitle(R.string.twenty_one_tweaks);
        } else {
            setTitle(R.string.twenty_one_tweaks);
            menuToggleModes.setTitle(R.string.app_name);
        }
    }

    private void toggleTweaksMenuItemVisibility() {
        if (menuToggleModes != null) {
            menuToggleModes.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    private void updateNightModeToggleIcon() {
        if (menuToggleNightMode == null) {
            return;
        }

        final boolean dark = Prefs.getInstance(this).isDarkMode();
        menuToggleNightMode.setIcon(dark ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
        menuToggleNightMode.setIconTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.colorOnPrimary)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toggle_modes) {
            inDailyDozenMode = !inDailyDozenMode;
            updateAppModeToggle();
            initDatePager();
            return true;
        } else if (itemId == R.id.menu_toggle_night_mode) {
            Prefs.getInstance(this).toggleDarkMode(this);
            recreate();
            return true;
        } else if (itemId == R.id.menu_latest_videos) {
            Common.openUrlInExternalBrowser(this, R.string.url_latest_videos);
            return true;
        } else if (itemId == R.id.menu_how_not_to_die) {
            Common.openUrlInExternalBrowser(this, R.string.url_how_not_to_die);
            return true;
        } else if (itemId == R.id.menu_cookbook) {
            Common.openUrlInExternalBrowser(this, R.string.url_cookbook);
            return true;
        } else if (itemId == R.id.menu_how_not_to_diet) {
            Common.openUrlInExternalBrowser(this, R.string.url_how_not_to_diet);
            return true;
        } else if (itemId == R.id.menu_daily_dozen_challenge) {
            Common.openUrlInExternalBrowser(this, R.string.url_daily_dozen_challenge);
            return true;
        } else if (itemId == R.id.menu_faq) {
            startActivity(new Intent(this, FaqActivity.class));
            return true;
        } else if (itemId == R.id.menu_donate) {
            Common.openUrlInExternalBrowser(this, R.string.url_donate);
            return true;
        } else if (itemId == R.id.menu_subscribe) {
            Common.openUrlInExternalBrowser(this, R.string.url_subscribe);
            return true;
        } else if (itemId == R.id.menu_open_source) {
            Common.openUrlInExternalBrowser(this, R.string.url_open_source);
            return true;
        } else if (itemId == R.id.menu_daily_reminder_settings) {
            startActivity(new Intent(this, DailyReminderSettingsActivity.class));
            return true;
        } else if (itemId == R.id.menu_backup) {
            backup();
            return true;
        } else if (itemId == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (itemId == R.id.menu_debug) {
            debugSettingsLauncher.launch(new Intent(this, DebugActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchForDateSelection(Intent intent) {
        dateSelectionLauncher.launch(intent);
    }

    private void initDatePager() {
        // Record user's current date selection (value is 0 when unset)
        int origDate = binding.datePager.getCurrentItem();

        final FragmentStateAdapter pagerAdapter = new DatePagerAdapter(getSupportFragmentManager(), getLifecycle(), inDailyDozenMode);

        binding.datePager.setAdapter(pagerAdapter);
        daysSinceEpoch = pagerAdapter.getItemCount();

        // Maintain user's selected date when switching adapters
        binding.datePager.setCurrentItem(origDate != 0 ? origDate : daysSinceEpoch, false);
    }

    private void backup() {
        if (!DDServings.isEmpty()) {
            new TaskRunner().executeAsync(new BackupTask(this, getBackupFile()));
        } else {
            Common.showToast(this, R.string.no_servings_recorded);
        }
    }

    private void checkIfOpenedForRestore(final Intent intent) {
        if (intent == null || !isRestoreIntent(intent) || alreadyHandledRestoreIntent || restoreInProgress) {
            return;
        }

        final Uri restoreFileUri = intent.getData();

        if (!DDServings.isEmpty()) {
            if (restoreConfirmDialogShown) {
                return;
            }

            restoreConfirmDialogShown = true;

            new AlertDialog.Builder(this)
                    .setTitle(R.string.restore_confirm_title)
                    .setMessage(R.string.restore_confirm_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        restore(restoreFileUri);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                        alreadyHandledRestoreIntent = true;
                        clearRestoreIntent();
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        } else {
            restore(restoreFileUri);
        }
    }

    private static boolean isRestoreIntent(final Intent intent) {
        return Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null;
    }

    private void clearRestoreIntent() {
        final Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            intent.setData(null);
            intent.setAction(Intent.ACTION_MAIN);
            setIntent(intent);
        }
    }

    private void restore(final Uri restoreFileUri) {
        restoreInProgress = true;
        new TaskRunner().executeAsync(new RestoreTask(this, restoreFileUri, getContentResolver()));
    }

    public File getBackupFile() {
        return new File(getFilesDir(), "dailydozen_backup.json");
    }

    private void shareBackupFile() {
        final File backupFile = getBackupFile();
        final String backupInstructions = TextUtils.join(System.lineSeparator(),
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
        restoreInProgress = false;

        if (event.isSuccess()) {
            alreadyHandledRestoreIntent = true;
            restoreConfirmDialogShown = false;
            clearRestoreIntent();
            initDatePager();
        } else {
            alreadyHandledRestoreIntent = false;
            restoreConfirmDialogShown = false;
        }

        Common.showToast(this, event.isSuccess() ? R.string.restore_success : R.string.restore_failed);
    }

    @Subscribe
    public void onEvent(CalculateStreaksTaskCompleteEvent event) {
        if (event.isSuccess()) {
            Prefs.getInstance(this).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
            initDatePager();
        }
    }

    @Subscribe
    public void onEvent(DisplayDateEvent event) {
        setDatePagerDate(event.getDate());
    }

    private void setDatePagerDate(final LocalDate date) {
        if (date != null) {
            Timber.d("Changing displayed date to %s", date.toString());
            binding.datePager.setCurrentItem(Day.getNumDaysSinceEpoch(date));
        }
    }

    @Override
    public void showProgressBar(int titleId) {
        binding.progressBarContainer.setVisibility(View.VISIBLE);
        binding.progressText.setText(titleId);
        binding.progressBar.setProgress(0);
    }

    @Override
    public void updateProgressBar(int current, int total) {
        binding.progressBar.setProgress(current);
        binding.progressBar.setMax(total);
    }

    @Override
    public void hideProgressBar() {
        binding.progressBarContainer.setVisibility(View.GONE);
        this.recreate();
    }
}