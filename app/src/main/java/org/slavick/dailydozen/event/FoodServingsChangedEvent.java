package org.slavick.dailydozen.event;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;

public class FoodServingsChangedEvent extends BaseEvent {
    private final Food food;
    private final Day day;

    public FoodServingsChangedEvent(Day day, Food food) {
        this.day = day;
        this.food = food;
    }

    public Day getDate() {
        return day;
    }

    public Food getFood() {
        return food;
    }
}
