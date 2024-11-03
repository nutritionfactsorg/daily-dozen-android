package org.nutritionfacts.dailydozen.event;

public class FoodServingsChangedEvent extends BaseEvent {
    private final long dateLong;
    private final String foodName;
    private final Boolean isVitamin;

    public FoodServingsChangedEvent(long dateLong, String foodName, Boolean isVitamin) {
        this.dateLong = dateLong;
        this.foodName = foodName;
        this.isVitamin = isVitamin;
    }

    public long getDateLong() {
        return dateLong;
    }

    public String getFoodName() {
        return foodName;
    }

    public Boolean getIsVitamin() {
        return isVitamin;
    }
}
