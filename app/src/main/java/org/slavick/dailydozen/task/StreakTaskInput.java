package org.slavick.dailydozen.task;

import org.slavick.dailydozen.model.Food;

import java.util.Date;

public class StreakTaskInput {
    private final Date startingDate;
    private final Food food;

    public StreakTaskInput(final Date startingDate, final Food food) {
        this.startingDate = startingDate;
        this.food = food;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public Food getFood() {
        return food;
    }
}
