package org.nutritionfacts.dailydozen.controller;

import com.github.mikephil.charting.data.CombinedData;

import org.greenrobot.eventbus.EventBus;
import org.nutritionfacts.dailydozen.event.BackupCompleteEvent;
import org.nutritionfacts.dailydozen.event.BaseEvent;
import org.nutritionfacts.dailydozen.event.CalculateStreaksTaskCompleteEvent;
import org.nutritionfacts.dailydozen.event.DisplayDateEvent;
import org.nutritionfacts.dailydozen.event.FoodServingsChangedEvent;
import org.nutritionfacts.dailydozen.event.LoadServingsHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.event.RestoreCompleteEvent;
import org.nutritionfacts.dailydozen.event.ShowExplodingStarAnimation;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;

public class Bus {
    public static void register(Object object) {
        EventBus.getDefault().register(object);
    }

    public static void unregister(Object object) {
        EventBus.getDefault().unregister(object);
    }

    private static void post(BaseEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void foodServingsChangedEvent(Day day, Food food) {
        post(new FoodServingsChangedEvent(day.getDateString(), food.getName()));
    }

    public static void displayLatestDate() {
        post(new DisplayDateEvent(Day.getToday()));
    }

    public static void showExplodingStarAnimation() {
        post(new ShowExplodingStarAnimation());
    }

    public static void restoreCompleteEvent(final boolean success) {
        post(new RestoreCompleteEvent(success));
    }

    public static void backupCompleteEvent(final boolean success) {
        post(new BackupCompleteEvent(success));
    }

    public static void calculateStreaksComplete(final boolean success) {
        post(new CalculateStreaksTaskCompleteEvent(success));
    }

    public static void loadServingsHistoryCompleteEvent(final CombinedData chartData) {
        post(new LoadServingsHistoryCompleteEvent(chartData));
    }
}
