package org.nutritionfacts.dailydozen.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.Servings;
import org.nutritionfacts.dailydozen.task.CalculateStreaksTask;

public class DatabaseUtil {
    public static void performUpgradesIfNecessary(final Context context) {
        calculateStreaksAfterDatabaseUpgradeToV2(context);
    }

    private static void calculateStreaksAfterDatabaseUpgradeToV2(final Context context) {
        if (!Prefs.getInstance(context).streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2()) {
            if (Servings.isEmpty()) {
                Prefs.getInstance(context).setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2();
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_streaks_title)
                        .setMessage(R.string.dialog_streaks_message)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new CalculateStreaksTask(context).execute();
                            }
                        })
                        .create().show();
            }
        }
    }
}
