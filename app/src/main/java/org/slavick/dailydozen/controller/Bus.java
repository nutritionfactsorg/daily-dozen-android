package org.slavick.dailydozen.controller;

import org.greenrobot.eventbus.EventBus;
import org.slavick.dailydozen.event.BaseEvent;
import org.slavick.dailydozen.event.DisplayDateEvent;
import org.slavick.dailydozen.event.FoodServingsChangedEvent;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;

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
}
