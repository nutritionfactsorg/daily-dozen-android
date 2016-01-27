package org.slavick.dailydozen.event;

import org.slavick.dailydozen.model.Food;

public class FoodServingsChangedEvent extends BaseEvent {
    private final Food food;

    public FoodServingsChangedEvent(Food food) {
        this.food = food;
    }

    public Food getFood() {
        return food;
    }
}
