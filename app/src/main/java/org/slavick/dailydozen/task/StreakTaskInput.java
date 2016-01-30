package org.slavick.dailydozen.task;

import org.slavick.dailydozen.model.Food;

import hirondelle.date4j.DateTime;

public class StreakTaskInput {
    private final DateTime startingDate;
    private final Food food;

    public StreakTaskInput(final DateTime startingDate, final Food food) {
        this.startingDate = startingDate;
        this.food = food;
    }

    public DateTime getStartingDate() {
        return startingDate;
    }

    public Food getFood() {
        return food;
    }
}
