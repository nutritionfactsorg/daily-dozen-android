package org.slavick.dailydozen.controller;

import org.slavick.dailydozen.event.BaseEvent;
import org.slavick.dailydozen.event.FoodServingsChangedEvent;
import org.slavick.dailydozen.model.Food;

import java.util.Date;

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

    public static void foodServingsChangedEvent(Date date, Food food) {
        post(new FoodServingsChangedEvent(date, food));
    }
}
