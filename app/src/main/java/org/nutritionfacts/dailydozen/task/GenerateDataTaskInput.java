package org.nutritionfacts.dailydozen.task;

public class GenerateDataTaskInput {
    private int historyToGenerate;
    private boolean generateRandomData;

    public GenerateDataTaskInput(int historyToGenerate, boolean generateRandomData) {
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
