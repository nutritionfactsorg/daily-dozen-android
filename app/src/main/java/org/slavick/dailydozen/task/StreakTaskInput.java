package org.slavick.dailydozen.task;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;

public class StreakTaskInput {
    private final Day startingDay;
    private final Food food;

    public StreakTaskInput(final Day startingDay, final Food food) {
        this.startingDay = startingDay;
        this.food = food;
    }

    public Day getStartingDay() {
        return startingDay;
    }

    public Food getFood() {
        return food;
    }
}
