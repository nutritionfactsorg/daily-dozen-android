package org.slavick.dailydozen.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupController {
    private final static String TAG = BackupController.class.getSimpleName();

    private Context context;

    public BackupController(Context context) {
        this.context = context;
    }

    public File getBackupFile() {
        return new File(context.getFilesDir(), "backup.csv");
    }

    public boolean backupToCsv() {
        final File backupFile = getBackupFile();
        Log.d(TAG, "backupFilename = " + backupFile.getName());
        try {
            final FileWriter fileWriter = new FileWriter(backupFile);

            final String headers = getHeadersLine() + getLineSeparator();
            Log.d(TAG, "headers = " + headers);
            fileWriter.write(headers);

            for (Day day : Day.getAllDays()) {
                final String dayLine = getDayLine(day) + getLineSeparator();
                Log.d(TAG, "dayLine = " + dayLine);
                fileWriter.write(dayLine);
            }

            Log.d(TAG, backupFile.getAbsolutePath() + " successfully written");
            fileWriter.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getHeadersLine() {
        final List<String> headers = new ArrayList<>();

        headers.add("Date");

        for (Food food : Food.getAllFoods()) {
            headers.add(food.getName());
        }

        return convertListToCsv(headers);
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

        return convertListToCsv(line);
    }

    private String convertListToCsv(final List<String> list) {
        return TextUtils.join(",", list);
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public void restoreFromCsv() {
        final File backupFile = getBackupFile();
        Log.d(TAG, "backupFilename = " + backupFile.getName());

        try {
            final FileInputStream restoreStream = context.openFileInput(backupFile.getName());

            BufferedReader reader = new BufferedReader(new InputStreamReader(restoreStream));
            String line = reader.readLine();
            Log.d(TAG, "restore line = " + line);
            while (line != null) {
                line = reader.readLine();
                Log.d(TAG, "restore line = " + line);
            }

            Log.d(TAG, backupFile.getAbsolutePath() + " successfully read");
            restoreStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
