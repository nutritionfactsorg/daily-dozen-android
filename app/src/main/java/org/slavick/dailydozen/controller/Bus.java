package org.slavick.dailydozen.controller;

import org.slavick.dailydozen.event.BaseEvent;
import org.slavick.dailydozen.event.FoodServingsChangedEvent;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;

import de.greenrobot.event.EventBus;

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
        post(new FoodServingsChangedEvent(day, food));
    }
}
