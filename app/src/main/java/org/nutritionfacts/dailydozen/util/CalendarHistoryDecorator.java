package org.nutritionfacts.dailydozen.util;

import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarHistoryDecorator implements DayViewDecorator {
    private final Set<LocalDate> days;
    private final ColorDrawable background;

    public CalendarHistoryDecorator(List<LocalDate> days, ColorDrawable background) {
        this.days = new HashSet<>(days);
        this.background = background;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return days.contains(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(background);
    }
}