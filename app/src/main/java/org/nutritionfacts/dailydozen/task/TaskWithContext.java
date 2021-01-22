package org.nutritionfacts.dailydozen.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import timber.log.Timber;

abstract class TaskWithContext<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Context context;

    ProgressDialog progress;

    TaskWithContext(final Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress = new ProgressDialog(getContext());
        progress.setIndeterminate(false);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // This will cancel the running AsyncTask
        progress.setCancelable(true);
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancel(true);
            }
        });
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        } catch (Exception e) {
            Timber.e("onPostExecute: Exception while trying to dismiss progress dialog");
        } finally {
            progress = null;
        }
    }
}
