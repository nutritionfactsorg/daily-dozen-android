package org.slavick.dailydozen.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.task.GenerateDataTask;

public class DebugActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        initClearDataButton();
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
                        .setCancelable(false)
                        .setTitle(R.string.debug_generate_random_data)
                        .setMessage(R.string.debug_generate_data_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new GenerateDataTask(DebugActivity.this).execute(generateRandomData);

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
}
