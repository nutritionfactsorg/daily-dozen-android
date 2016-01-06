package org.slavick.dailydozen.controller;

import android.text.TextUtils;
import android.util.Log;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupController {
    private final static String TAG = BackupController.class.getSimpleName();

    public void backupToCsv() {
        // TODO: 1/5/16 ask for permission to write

        final String headers = getHeadersLine();
        Log.d(TAG, "headers = " + headers);

        for (Day day : Day.getAllDays()) {
            final String dayLine = getDayLine(day);
            Log.d(TAG, "dayLine = " + dayLine);
        }
    }

    private String getHeadersLine() {
        final List<String> headers = new ArrayList<>();

        headers.add("Date");

        for (Food food : Food.getAllFoods()) {
            headers.add(food.getName());
        }

        return TextUtils.join(",", headers);
    }

    private String getDayLine(Day day) {
        final List<String> line = new ArrayList<>();

        line.add(String.valueOf(day.getDate()));

        final Date date = day.getDateObject();

        // TODO: 1/5/16 this is horribly inefficient, but good enough for now
        for (Food food : Food.getAllFoods()) {
            final Servings serving = Servings.getByDateAndFood(date, food);
            line.add(serving != null ? String.valueOf(serving.getServings()) : "0");
        }

        return TextUtils.join(",", line);
    }
}
