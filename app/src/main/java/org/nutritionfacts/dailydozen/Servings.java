package org.nutritionfacts.dailydozen;

public interface Servings {
    int getServings();
    void recalculateStreak();
    Long save();
}
