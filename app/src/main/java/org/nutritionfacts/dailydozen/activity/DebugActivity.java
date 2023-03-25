package org.nutritionfacts.dailydozen.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.PermissionController;
import org.nutritionfacts.dailydozen.databinding.ActivityDebugBinding;
import org.nutritionfacts.dailydozen.task.GenerateDataTask;
import org.nutritionfacts.dailydozen.task.ProgressListener;
import org.nutritionfacts.dailydozen.task.TaskRunner;
import org.nutritionfacts.dailydozen.task.params.GenerateDataTaskParams;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import timber.log.Timber;

public class DebugActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ProgressListener {
    private ActivityDebugBinding binding;

    private ProgressDialog progressDialog;

    private int historyToGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDebugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initHistoryToGenerateSpinner();

        onClearDataClicked();
        onGenerateFullDataClicked();
        onGenerateRandomDataClicked();
        onShowNotificationClicked();
    }

    private void initHistoryToGenerateSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.history_to_generate_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.historyToGenerateSpinner.setOnItemSelectedListener(this);
        binding.historyToGenerateSpinner.setAdapter(adapter);

        binding.historyToGenerateSpinner.setSelection(1); // Select 3 months by default
    }

    public void onClearDataClicked() {
        binding.debugClearData.setOnClickListener(v -> new AlertDialog.Builder(DebugActivity.this)
                .setTitle(R.string.debug_clear_data)
                .setMessage(R.string.debug_clear_data_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    Common.truncateAllDatabaseTables();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create().show());
    }

    public void onGenerateFullDataClicked() {
        binding.debugGenerateFullData.setOnClickListener(v -> generateData(false));
    }

    public void onGenerateRandomDataClicked() {
        binding.debugGenerateRandomData.setOnClickListener(v -> generateData(true));
    }

    private void generateData(final boolean generateRandomData) {
        new AlertDialog.Builder(DebugActivity.this)
                .setTitle(generateRandomData ? R.string.debug_generate_random_data : R.string.debug_generate_full_data)
                .setMessage(R.string.debug_generate_data_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    final GenerateDataTaskParams taskParams = new GenerateDataTaskParams(historyToGenerate, generateRandomData);

                    new TaskRunner().executeAsync(new GenerateDataTask(this, taskParams));

                    dialog.dismiss();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.historyToGenerate = getNumDaysForChoice();
    }

    private int getNumDaysForChoice() {
        switch (binding.historyToGenerateSpinner.getSelectedItemPosition()) {
            case 1:
                return 90;
            case 2:
                return 180;
            case 4:
                return 730;
            case 5:
                return 1825;
            case 0:
                return 30;
            case 3:
            default:
                return 365;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onShowNotificationClicked() {
        binding.debugShowNotification.setOnClickListener(v -> {
            Timber.d("onShowNotificationClicked");
            if (PermissionController.canPostNotifications(this)) {
                Timber.d("canPostNotifications = [true]");
                NotificationUtil.showUpdateReminderNotification(this, null);
            } else {
                Timber.d("canPostNotifications = [false]");
                PermissionController.askForPostNotifications(this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionController.grantedPostNotifications(requestCode, grantResults)) {
            Timber.d("onRequestPermissionsResult = [true]");
            NotificationUtil.showUpdateReminderNotification(this, null);
        } else {
            Timber.d("onRequestPermissionsResult = [false]");
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
