package org.slavick.dailydozen.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.task.GenerateDataTask;
import org.slavick.dailydozen.task.GenerateDataTaskInput;

public class DebugActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner historyToGenerateSpinner;

    private int historyToGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        historyToGenerateSpinner = (Spinner) findViewById(R.id.history_to_generate_spinner);

        initClearDataButton();
        initHistoryToGenerateSpinner();
        initGenerateFullDataButton();
        initGenerateRandomDataButton();
    }

    private void initClearDataButton() {
        final Button btnClearData = (Button) findViewById(R.id.debug_clear_data);
        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DebugActivity.this)
                        .setTitle(R.string.debug_clear_data)
                        .setMessage(R.string.debug_clear_data_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Servings.truncate(Servings.class);
                                Day.truncate(Day.class);

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
        });
    }

    private void initHistoryToGenerateSpinner() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.history_to_generate_choices, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        historyToGenerateSpinner = (Spinner) findViewById(R.id.history_to_generate_spinner);
        historyToGenerateSpinner.setOnItemSelectedListener(this);
        historyToGenerateSpinner.setAdapter(adapter);

        historyToGenerateSpinner.setSelection(3); // Select 1 year by default
    }

    private void initGenerateFullDataButton() {
        final Button btnGenerateFullData = (Button) findViewById(R.id.debug_generate_full_data);
        btnGenerateFullData.setOnClickListener(getGenerateDataClickListener(false));
    }

    private void initGenerateRandomDataButton() {
        final Button btnGenerateRandomData = (Button) findViewById(R.id.debug_generate_random_data);
        btnGenerateRandomData.setOnClickListener(getGenerateDataClickListener(true));
    }

    private View.OnClickListener getGenerateDataClickListener(final boolean generateRandomData) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DebugActivity.this)
                        .setTitle(R.string.debug_generate_random_data)
                        .setMessage(R.string.debug_generate_data_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final GenerateDataTaskInput taskInput = new GenerateDataTaskInput(historyToGenerate, generateRandomData);

                                new GenerateDataTask(DebugActivity.this).execute(taskInput);

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
        };
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
}
