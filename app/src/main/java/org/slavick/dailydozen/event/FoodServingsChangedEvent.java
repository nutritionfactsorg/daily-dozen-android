package org.slavick.dailydozen.event;

public class FoodServingsChangedEvent extends BaseEvent {
    private final String dateString;
    private final String foodName;

    public FoodServingsChangedEvent(String dateString, String foodName) {
        this.dateString = dateString;
        this.foodName = foodName;
    }

    public String getDateString() {
        return dateString;
    }

    public String getFoodName() {
        return foodName;
    }
}
