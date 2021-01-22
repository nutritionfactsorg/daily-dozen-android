package org.nutritionfacts.dailydozen.task.params;

public class GenerateDataTaskParams {
    private int historyToGenerate;
    private boolean generateRandomData;

    public GenerateDataTaskParams(int historyToGenerate, boolean generateRandomData) {
        this.historyToGenerate = historyToGenerate;
        this.generateRandomData = generateRandomData;
    }

    public int getHistoryToGenerate() {
        return historyToGenerate;
    }

    public boolean generateRandomData() {
        return generateRandomData;
    }
}
