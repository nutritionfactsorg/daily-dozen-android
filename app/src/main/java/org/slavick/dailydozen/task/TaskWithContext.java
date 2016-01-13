package org.slavick.dailydozen.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class TaskWithContext<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Context context;

    protected ProgressDialog progress;

    public TaskWithContext(final Context context) {
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
        progress.show();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        progress.hide();
    }
}
