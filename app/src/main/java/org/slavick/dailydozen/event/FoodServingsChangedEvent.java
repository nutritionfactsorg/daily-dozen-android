package org.slavick.dailydozen.event;

import org.slavick.dailydozen.model.Food;

import hirondelle.date4j.DateTime;

public class FoodServingsChangedEvent extends BaseEvent {
    private final Food food;
    private final DateTime dateTime;

    public FoodServingsChangedEvent(DateTime dateTime, Food food) {
        this.dateTime = dateTime;
        this.food = food;
    }

    public DateTime getDate() {
        return dateTime;
    }

    public Food getFood() {
        return food;
    }
}
