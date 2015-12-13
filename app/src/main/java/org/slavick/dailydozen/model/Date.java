package org.slavick.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Table(name = "dates")
public class Date extends Model {
    @Column(name = "date", index = true)
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String dateString = "";

        try {
            java.util.Date javaDate = new SimpleDateFormat("yyyyMMdd").parse(date);
            dateString = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(javaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public void setDateToToday() {
        setDate(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
    }
}
