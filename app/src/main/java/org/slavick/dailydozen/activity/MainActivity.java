package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.DatePagerAdapter;
import org.slavick.dailydozen.controller.PermissionController;
import org.slavick.dailydozen.task.BackupTask;

import java.io.File;

public class MainActivity extends AppCompatActivity implements BackupTask.Listener {
    protected ViewPager datePager;

    private DatePagerAdapter datePagerAdapter;

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
        datePagerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_backup_restore:
                backup();
                return true;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDatePager() {
        datePagerAdapter = new DatePagerAdapter(getSupportFragmentManager());
        datePager.setAdapter(datePagerAdapter);

        // Go to today's date by default
        datePager.setCurrentItem(datePagerAdapter.getCount(), false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionController.grantedWriteExternalStorage(requestCode, permissions, grantResults)) {
            backup();
        } else {
            Common.showToast(this, getString(R.string.permission_needed_to_write_storage));
        }
    }

    private void backup() {
        if (PermissionController.canWriteExternalStorage(this)) {
            new BackupTask(this, this).execute(getBackupFile());
        } else {
            PermissionController.askForWriteExternalStorage(this);
        }
    }

    public File getBackupFile() {
        return new File(getFilesDir(), "dailydozen_backup.csv");
    }

    private void shareBackupFile() {
        final File backupFile = getBackupFile();
        final String backupInstructions = getString(R.string.backup_instructions);
        final Uri backupFileUri = FileProvider.getUriForFile(this, Common.FILE_PROVIDER_AUTHORITY, backupFile);

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, backupFile.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, backupInstructions);
        shareIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
        shareIntent.setType(getString(R.string.backup_mimetype));
        startActivity(shareIntent);
    }

    @Override
    public void onBackupComplete(boolean success) {
        if (success) {
            shareBackupFile();
        }
    }
}
