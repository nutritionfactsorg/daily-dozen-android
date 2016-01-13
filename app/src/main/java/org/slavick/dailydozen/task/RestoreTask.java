package org.slavick.dailydozen.task;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.slavick.dailydozen.Common;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

public class RestoreTask extends TaskWithContext<Uri, Integer, Boolean> {
    private final static String TAG = RestoreTask.class.getSimpleName();

    private final Listener listener;

    public interface Listener {
        void onRestoreComplete(boolean success);
    }

    public RestoreTask(Context context, Listener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Uri... params) {
        try {
            final InputStream restoreInputStream = getContext().getContentResolver().openInputStream(params[0]);

            if (restoreInputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));
                String line = reader.readLine();
                Log.d(TAG, "restore line = " + line);

                String[] headers = line.split(",");

                Day.deleteAllDays();

                while (line != null) {
                    if (isCancelled()) {
                        break;
                    }

                    line = reader.readLine();

                    if (line != null) {
                        Log.d(TAG, "restore line = " + line);

                        final String[] values = line.split(",");
                        final Day day = Day.createDateIfDoesNotExist(Long.valueOf(values[0]));
                        final Date date = day.getDateObject();

                        for (int i = 1; i < headers.length; i++) {
                            final Integer numServings = Integer.valueOf(values[i]);
                            if (numServings > 0) {
                                Servings.createServingsIfDoesNotExist(date, Food.getByName(headers[i]), numServings);
                            }
                        }
                    }
                }

                restoreInputStream.close();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        final Context context = getContext();
        Common.showToast(context, context.getString(success ? R.string.restore_success : R.string.restore_failed));

        if (listener != null) {
            listener.onRestoreComplete(success);
        }
    }
}
