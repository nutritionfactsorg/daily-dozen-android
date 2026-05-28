package org.nutritionfacts.dailydozen.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class TaskRunner {
    private static final TaskRunner INSTANCE = new TaskRunner();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor executor = Executors.newCachedThreadPool();

    private TaskRunner() {
    }

    public static TaskRunner getInstance() {
        return INSTANCE;
    }

    public static void updateProgress(ProgressListener listener, int current, int total) {
        getInstance().handler.post(() -> listener.updateProgressBar(current, total));
    }

    public <R> void executeAsync(CustomCallable<R> callable) {
        try {
            callable.setUiForLoading();
            executor.execute(new RunnableTask<>(handler, callable));
        } catch (Exception e) {
            Timber.e(e, "executeAsync failed");
        }
    }

    public static class RunnableTask<R> implements Runnable {
        private final Handler handler;
        private final CustomCallable<R> callable;

        public RunnableTask(Handler handler, CustomCallable<R> callable) {
            this.handler = handler;
            this.callable = callable;
        }

        @Override
        public void run() {
            try {
                final R result = callable.call();
                handler.post(new RunnableTaskForHandler<>(callable, result));
            } catch (Exception e) {
                Timber.e(e, "background task failed");
            }
        }
    }

    public static class RunnableTaskForHandler<R> implements Runnable {
        private final CustomCallable<R> callable;
        private final R result;

        public RunnableTaskForHandler(CustomCallable<R> callable, R result) {
            this.callable = callable;
            this.result = result;
        }

        @Override
        public void run() {
            callable.setDataAfterLoading(result);
        }
    }
}
