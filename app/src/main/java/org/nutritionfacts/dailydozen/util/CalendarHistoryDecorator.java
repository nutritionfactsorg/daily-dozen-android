package org.nutritionfacts.dailydozen.util;

import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hirondelle.date4j.DateTime;

public class CalendarHistoryDecorator implements DayViewDecorator {
    private final Set<DateTime> days;
    private final ColorDrawable background;

    public CalendarHistoryDecorator(List<DateTime> days, ColorDrawable background) {
        this.days = new HashSet<>(days);
        this.background = background;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // The DateTime object must be created with the time specified as zeros
        return days.contains(new DateTime(day.getYear(), day.getMonth(), day.getDay(), 0, 0, 0, 0));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(background);
    }
}