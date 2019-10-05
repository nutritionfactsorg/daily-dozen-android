package org.nutritionfacts.dailydozen.model.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class TimeScale {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DAYS, MONTHS, YEARS})
    public @interface Interface { }

    public static final int DAYS = 0;
    public static final int MONTHS = 1;
    public static final int YEARS = 2;
}
