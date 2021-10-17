package org.nutritionfacts.dailydozen.task.params;

import org.nutritionfacts.dailydozen.model.enums.HistoryType;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;

public class LoadHistoryTaskParams {
    @HistoryType.Interface
    private final int historyType;

    @TimeScale.Interface
    private final int timeScale;

    private final int selectedYear;
    private final int selectedMonth;

    public LoadHistoryTaskParams(@HistoryType.Interface int historyType, @TimeScale.Interface int timeScale, int selectedYear, int selectedMonth) {
        this.historyType = historyType;
        this.timeScale = timeScale;
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
    }

    @HistoryType.Interface
    public int getHistoryType() {
        return historyType;
    }

    @TimeScale.Interface
    public int getTimeScale() {
        return timeScale;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public int getSelectedMonth() {
        return selectedMonth;
    }
}
