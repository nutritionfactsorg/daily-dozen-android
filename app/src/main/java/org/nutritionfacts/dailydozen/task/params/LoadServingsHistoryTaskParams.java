package org.nutritionfacts.dailydozen.task.params;

import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class LoadServingsHistoryTaskParams {
    @TimeScale.Interface
    private int timeScale;

    private int selectedYear;
    private int selectedMonth;

    public LoadServingsHistoryTaskParams(@TimeScale.Interface int timeScale, int selectedYear, int selectedMonth) {
        this.timeScale = timeScale;
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
    }

    @TimeScale.Interface
    public int getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(@TimeScale.Interface int timeScale) {
        this.timeScale = timeScale;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int selectedMonth) {
        this.selectedMonth = selectedMonth;
    }
}
