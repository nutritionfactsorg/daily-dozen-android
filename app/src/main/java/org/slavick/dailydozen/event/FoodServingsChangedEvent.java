package org.slavick.dailydozen.event;

import org.slavick.dailydozen.model.Food;

import java.util.Date;

public class FoodServingsChangedEvent extends BaseEvent {
    private final Food food;
    private final Date date;

    public FoodServingsChangedEvent(Date date, Food food) {
        this.date = date;
        this.food = food;
    }

    public Date getDate() {
        return date;
    }

    public Food getFood() {
        return food;
    }
}
