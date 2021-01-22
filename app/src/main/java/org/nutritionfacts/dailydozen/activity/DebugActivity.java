package org.nutritionfacts.dailydozen.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.task.GenerateDataTask;
import org.nutritionfacts.dailydozen.task.params.GenerateDataTaskParams;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DebugActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.history_to_generate_spinner)
    protected Spinner historyToGenerateSpinner;
    @BindView(R.id.debug_show_notification)
    protected Button showNotificationButton;

    private int historyToGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        ButterKnife.bind(this);

        initHistoryToGenerateSpinner();
    }

    private void initHistoryToGenerateSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.history_to_generate_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        historyToGenerateSpinner.setOnItemSelectedListener(this);
        historyToGenerateSpinner.setAdapter(adapter);

        historyToGenerateSpinner.setSelection(1); // Select 3 months by default
    }

    @OnClick(R.id.debug_clear_data)
    public void onClearDataClicked() {
        new AlertDialog.Builder(DebugActivity.this)
                .setTitle(R.string.debug_clear_data)
                .setMessage(R.string.debug_clear_data_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.truncateAllDatabaseTables();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @OnClick(R.id.debug_generate_full_data)
    public void onGenerateFullDataClicked() {
        generateData(false);
    }

    @OnClick(R.id.debug_generate_random_data)
    public void onGenerateRandomDataClicked() {
        generateData(true);
    }

    private void generateData(final boolean generateRandomData) {
        new AlertDialog.Builder(DebugActivity.this)
                .setTitle(generateRandomData ? R.string.debug_generate_random_data : R.string.debug_generate_full_data)
                .setMessage(R.string.debug_generate_data_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final GenerateDataTaskParams taskParams = new GenerateDataTaskParams(historyToGenerate, generateRandomData);

                        new GenerateDataTask(DebugActivity.this).execute(taskParams);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final int historyToGenerate = getNumDaysForChoice();
        Common.showToast(view.getContext(), String.valueOf(historyToGenerate));
        this.historyToGenerate = historyToGenerate;
    }

    private int getNumDaysForChoice() {
        switch (historyToGenerateSpinner.getSelectedItemPosition()) {
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

    @OnClick(R.id.debug_show_notification)
    public void onShowNotificationClicked() {
        NotificationUtil.showUpdateReminderNotification(DebugActivity.this, null);
    }
}
