package org.slavick.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.BackupController;

public class BackupRestoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        initBackupButton();
    }

    private void initBackupButton() {
        final Button btnBackup = (Button) findViewById(R.id.backup);

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showNotImplementedYet(BackupRestoreActivity.this);

                final BackupController backupController = new BackupController();
                backupController.backupToCsv();
            }
        });
    }
}
