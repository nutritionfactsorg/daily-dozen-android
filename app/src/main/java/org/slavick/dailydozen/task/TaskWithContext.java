package org.slavick.dailydozen.task;

import android.content.Context;
import android.os.AsyncTask;

public abstract class TaskWithContext<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private Context context;

    public TaskWithContext(final Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
