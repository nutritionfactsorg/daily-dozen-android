package org.slavick.dailydozen.helper;

import com.activeandroid.query.Select;

import org.slavick.dailydozen.model.Date;

import java.text.SimpleDateFormat;

public class Dates {
    private static boolean dateExists(String dateString) {
        return new Select().from(Date.class).where("date = ?", dateString).exists();
    }

    public static Date today() {
        return createDateIfDoesNotExist(formatDateString(new java.util.Date()));
    }

    public static Date getByDate(String dateString) {
        return new Select().from(Date.class).where("date = ?", dateString).executeSingle();
    }

    private static String formatDateString(java.util.Date date) {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public static Date createDateIfDoesNotExist(final String dateString) {
        Date date;

        if (!dateExists(dateString)) {
            date = new Date();
            date.setDate(dateString);
        } else {
            date = getByDate(dateString);
        }

        return date;
    }
}
