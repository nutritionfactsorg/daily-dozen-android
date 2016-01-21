package org.slavick.dailydozen;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {
    private final Queue<Integer> window = new LinkedList<>();
    private final int period;
    private int sum = 0;

    public MovingAverage(int period) {
        this.period = period;
    }

    public void add(int num) {
        window.add(num);

        sum += num;

        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public float getAverage() {
        return window.isEmpty() ? 0f : (float) sum / window.size();
    }
}