package org.nutritionfacts.dailydozen.model.enums;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class HistoryType {
    @Retention(RetentionPolicy.SOURCE)
    public @interface Interface { }

    public static final int FoodServings = 0;
    public static final int Tweaks = 1;
    public static final int Weights = 2;
}
