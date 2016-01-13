package org.slavick.dailydozen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.controller.PermissionController;
import org.slavick.dailydozen.task.BackupTask;
import org.slavick.dailydozen.task.RestoreTask;

import java.io.File;

public class BackupRestoreActivity extends AppCompatActivity implements BackupTask.Listener, RestoreTask.Listener {
    private static final String AUTHORITY = "org.slavick.dailydozen.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        // Set the title because the title is set to Daily Dozen in the manifest. That is done so that they open with
        // dialog says "Open with Daily Dozen" instead of something else.
        setTitle(R.string.backup_restore);

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

    public File getBackupFile() {
        return new File(getFilesDir(), "dailydozen_backup.csv");
    }

    private void backup() {
        new BackupTask(this, this).execute(getBackupFile());
    }

    private void shareBackupFile() {
        final File backupFile = getBackupFile();
        final String backupInstructions = getString(R.string.backup_instructions);
        final Uri backupFileUri = FileProvider.getUriForFile(this, AUTHORITY, backupFile);

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, backupFile.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, backupInstructions);
        shareIntent.putExtra(Intent.EXTRA_STREAM, backupFileUri);
        shareIntent.setType(getString(R.string.backup_mimetype));
        startActivity(shareIntent);
    }

    private void initRestoreButton() {
        final Button btnRestore = (Button) findViewById(R.id.restore);

        final Uri restoreFileUri = getIntent().getData();

        if (restoreFileUri != null) {
            btnRestore.setEnabled(true);
            btnRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(BackupRestoreActivity.this)
                            .setTitle(getString(R.string.restore_confirm))
                            .setMessage(getString(R.string.restore_confirm_message))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    restore(restoreFileUri);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            });
        } else {
            btnRestore.setEnabled(false);
        }
    }

    private void restore(final Uri restoreFileUri) {
        new RestoreTask(this, this).execute(restoreFileUri);
    }

    @Override
    public void onBackupComplete(boolean success) {
        if (success) {
            Common.showToast(this, getString(R.string.backup_success));
            shareBackupFile();
        } else {
            Common.showToast(this, getString(R.string.backup_failed));
        }
    }

    @Override
    public void onRestoreComplete(boolean success) {
        if (success) {
            // TODO: 1/11/16 this leaves an extra copy of Daily Dozen running or something. Restore a file and then
            // press the multitasking button
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
