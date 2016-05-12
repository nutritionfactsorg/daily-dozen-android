package org.nutritionfacts.dailydozen.controller;

import android.content.Context;
import android.content.SharedPreferences;

import org.nutritionfacts.dailydozen.Common;

public class Prefs {
    private static final String FOODS_HAVE_BEEN_CREATED = "foods_have_been_created";
    private static final String STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2 = "v2_streaks_calculated";
    private static final String USER_HAS_SEEN_FIRST_STAR_EXPLOSION = "user_has_seen_first_star_explosion";

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

    private boolean getBooleanPref(final String name) {
        return sharedPrefs.getBoolean(name, false);
    }

    private void setBooleanPref(final String name, final boolean value) {
        sharedPrefs.edit().putBoolean(name, value).apply();
    }

    public boolean foodsHaveBeenCreated() {
        return getBooleanPref(FOODS_HAVE_BEEN_CREATED);
    }

    public void setFoodsHaveBeenCreated() {
        setBooleanPref(FOODS_HAVE_BEEN_CREATED, true);
    }

    public boolean streaksHaveBeenCalculatedAfterDatabaseUpgradeToV2() {
        return getBooleanPref(STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2);
    }

    public void setStreaksHaveBeenCalculatedAfterDatabaseUpgradeToV2() {
        setBooleanPref(STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2, true);
    }

    public boolean userHasSeenFirstStarExplosion() {
        return getBooleanPref(USER_HAS_SEEN_FIRST_STAR_EXPLOSION);
    }

    public void setUserHasSeenFirstStarExplosion() {
        setBooleanPref(USER_HAS_SEEN_FIRST_STAR_EXPLOSION, true);
    }
}