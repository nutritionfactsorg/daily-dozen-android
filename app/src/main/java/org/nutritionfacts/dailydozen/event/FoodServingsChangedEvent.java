package org.nutritionfacts.dailydozen.event;

public class FoodServingsChangedEvent extends BaseEvent {
    private final String dateString;
    private final String foodName;
    private final Boolean isVitamin;

    public FoodServingsChangedEvent(String dateString, String foodName, Boolean isVitamin) {
        this.dateString = dateString;
        this.foodName = foodName;
        this.isVitamin = isVitamin;
    }

    public String getDateString() {
        return dateString;
    }

    public String getFoodName() {
        return foodName;
    }

    public Boolean getIsVitamin() {
        return isVitamin;
    }
}
