package org.nutritionfacts.dailydozen.task;

public interface ProgressListener {
    void showProgressBar(int titleId);

    void updateProgressBar(int current, int total);

    void hideProgressBar();
}
