package org.slavick.dailydozen.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.task.RestoreTask;

public class BackupRestoreActivity extends AppCompatActivity implements RestoreTask.Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);

        // Set the title because the title is set to Daily Dozen in the manifest. That is done so that they open with
        // dialog says "Open with Daily Dozen" instead of something else.
        setTitle(R.string.backup_restore);

        initRestoreButton();
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
                            .setTitle(getString(R.string.restore_confirm_title))
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
    public void onRestoreComplete(boolean success) {
        if (success) {
            // TODO: 1/11/16 this leaves an extra copy of Daily Dozen running or something. Restore a file and then
            // press the multitasking button
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
