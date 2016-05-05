package org.nutritionfacts.dailydozen.controller;

import android.content.Context;
import android.content.SharedPreferences;

import org.nutritionfacts.dailydozen.Common;

public class Prefs {
    private static final String FOODS_HAVE_BEEN_CREATED = "foods_have_been_created";
    private static final String STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2 = "v2_streaks_calculated";

    private static Prefs instance;

    private SharedPreferences sharedPrefs;

    public Prefs(final Context context) {
        this.sharedPrefs = context.getSharedPreferences(Common.PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static Prefs getInstance(final Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }

        return instance;
    }

    private void setBooleanPref(final String name, final boolean value) {
        sharedPrefs.edit().putBoolean(name, value).apply();
    }

    public boolean foodsHaveBeenCreated() {
        return sharedPrefs.getBoolean(FOODS_HAVE_BEEN_CREATED, false);
    }

    public void setFoodsHaveBeenCreated() {
        setBooleanPref(FOODS_HAVE_BEEN_CREATED, true);
    }

    public boolean streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2() {
        return sharedPrefs.getBoolean(STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2, false);
    }

    public void setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2() {
        setBooleanPref(STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2, true);
    }
}