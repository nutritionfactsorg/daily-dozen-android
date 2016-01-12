package org.slavick.dailydozen.controller;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.model.Servings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
        return new File(context.getFilesDir(), "dailydozen_backup.csv");
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

    public boolean restoreFromCsv(final Uri restoreFileUri) {
        try {
            final InputStream restoreInputStream = context.getContentResolver().openInputStream(restoreFileUri);

            if (restoreInputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(restoreInputStream));
                String line = reader.readLine();
                Log.d(TAG, "restore line = " + line);

                String[] headers = line.split(",");

                Day.deleteAllDays();

                while (line != null) {
                    line = reader.readLine();

                    if (line != null) {
                        Log.d(TAG, "restore line = " + line);

                        final String[] values = line.split(",");
                        final Day day = Day.createDateIfDoesNotExist(Long.valueOf(values[0]));
                        final Date date = day.getDateObject();

                        for (int i = 1; i < headers.length; i++) {
                            final Integer numServings = Integer.valueOf(values[i]);
                            if (numServings > 0) {
                                Servings.createServingsIfDoesNotExist(date, Food.getByName(headers[i]), numServings);
                            }
                        }
                    }
                }

                restoreInputStream.close();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }
}
