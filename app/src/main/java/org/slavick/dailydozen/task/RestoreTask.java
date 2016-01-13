package org.slavick.dailydozen.task;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        // TODO: 1/12/16 Clean up this code, it is pretty rough

        try {
            final InputStream restoreInputStream = getContext().getContentResolver().openInputStream(params[0]);

            if (restoreInputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));
                String line = reader.readLine();
                Log.d(TAG, "restore line = " + line);

                String[] headers = line.split(",");

                Day.deleteAllDays();

                final List<String> lines = new ArrayList<>();
                while (line != null) {
                    if (isCancelled()) {
                        break;
                    }

                    line = reader.readLine();

                    if (!TextUtils.isEmpty(line)) {
                        lines.add(line);
                    }
                }

                final int numLines = lines.size();

                for (int i = 0; i < numLines; i++) {
                    line = lines.get(i);

                    Log.d(TAG, "restore line = " + line);

                    final String[] values = line.split(",");
                    final Day day = Day.createDateIfDoesNotExist(Long.valueOf(values[0]));
                    final Date date = day.getDateObject();

                    for (int j = 1; j < headers.length; j++) {
                        final Integer numServings = Integer.valueOf(values[j]);
                        if (numServings > 0) {
                            Servings.createServingsIfDoesNotExist(date, Food.getByName(headers[j]), numServings);
                        }
                    }

                    publishProgress(i + 1, numLines);
                }

                reader.close();
                restoreInputStream.close();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (values.length == 2) {
            progress.setProgress(values[0]);
            progress.setMax(values[1]);
        }
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
