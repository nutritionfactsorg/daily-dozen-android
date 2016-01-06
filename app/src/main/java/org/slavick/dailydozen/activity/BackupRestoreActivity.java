package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.BackupController;
import org.slavick.dailydozen.controller.PermissionController;

public class BackupRestoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        initBackupButton();
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
        final BackupController backupController = new BackupController();
        backupController.backupToCsv();
    }
}
