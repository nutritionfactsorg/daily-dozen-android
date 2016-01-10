package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.BackupController;
import org.slavick.dailydozen.controller.PermissionController;

public class BackupRestoreActivity extends AppCompatActivity {
    private static final String TAG = BackupRestoreActivity.class.getSimpleName();

    public static final int BACKUP_FILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        initBackupButton();
        initRestoreButton();
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

    private void initBackupButton() {
        final Button btnBackup = (Button) findViewById(R.id.backup);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionController.canWriteExternalStorage(BackupRestoreActivity.this)) {
                    backup();
                } else {
                    PermissionController.askForWriteExternalStorage(BackupRestoreActivity.this);
                }
            }
        });
    }

    private void backup() {
        final BackupController backupController = new BackupController(this);
        final boolean backupSuccess = backupController.backupToCsv();

        if (backupSuccess) {
            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "DailyDozen backup");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "To restore this backup file, 1. ensure DailyDozen is installed, 2. tap on the file");

            final Uri backupFileUri = FileProvider.getUriForFile(this, "org.slavick.dailydozen.fileprovider", backupController.getBackupFile());
            Log.d(TAG, "backupFileUri = " + backupFileUri.toString());

            shareIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
            shareIntent.setType("message/rfc822");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(shareIntent, BACKUP_FILE_REQUEST);
        }
    }

    private void initRestoreButton() {
        final Button btnRestore = (Button) findViewById(R.id.restore);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });
    }

    private void restore() {
        final BackupController backupController = new BackupController(this);
        backupController.restoreFromCsv();
    }
}
