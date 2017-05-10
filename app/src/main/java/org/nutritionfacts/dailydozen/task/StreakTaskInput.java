package org.nutritionfacts.dailydozen.task;

import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

public class StreakTaskInput {
    private final Day startingDay;
    private final Food food;

    public StreakTaskInput(final Day startingDay, final Food food) {
        this.startingDay = startingDay;
        this.food = food;
    }

    Day getStartingDay() {
        return startingDay;
    }

    public Food getFood() {
        return food;
    }
}
