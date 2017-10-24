package org.nutritionfacts.dailydozen.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import com.google.gson.Gson;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.model.enums.Units;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;

import java.util.HashSet;
import java.util.Set;

import hugo.weaving.DebugLog;

public class Prefs {
    private static final String STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2 = "v2_streaks_calculated";
    private static final String USER_HAS_SEEN_FIRST_STAR_EXPLOSION = "user_has_seen_first_star_explosion";
    private static final String PREF_UPDATE_REMINDER = "pref_update_reminder";
    private static final String DEFAULT_UPDATE_REMINDER_CREATED = "default_update_reminder_created";
    private static final String UNIT_TYPE = "unit_type";

    private static Prefs instance;

    private SharedPreferences sharedPrefs;
    private static final Gson gson = new Gson();

    private Prefs(final Context context) {
        this.sharedPrefs = context.getSharedPreferences(Common.PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static Prefs getInstance(final Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }

        return instance;
    }

    private void removePref(final String name) {
        sharedPrefs.edit().remove(name).apply();
    }

    private String getStringPref(final String name) {
        return sharedPrefs.getString(name, "");
    }

    private Set<String> getStringSetPref(final String name) {
        return sharedPrefs.getStringSet(name, null);
    }

    private void setStringPref(final String name, final String value) {
        sharedPrefs.edit().putString(name, value).apply();
    }

    private void setStringSetPref(final String name, final Set<String> value) {
        sharedPrefs.edit().putStringSet(name, value).apply();
    }

    private boolean getBooleanPref(final String name) {
        return sharedPrefs.getBoolean(name, false);
    }

    private void setBooleanPref(final String name, final boolean value) {
        sharedPrefs.edit().putBoolean(name, value).apply();
    }

    private void setIntegerPref(final String name, final int value) {
        sharedPrefs.edit().putInt(name, value).apply();
    }

    private int getIntegerPref(final String name) {
        return sharedPrefs.getInt(name, 0);
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

    @DebugLog
    public void setUpdateReminderPref(Set<UpdateReminderPref> prefs) {
        Set<String> jsonSet = new HashSet<>();
        for (UpdateReminderPref pref : prefs) {
            jsonSet.add(gson.toJson(pref));
        }
        setStringSetPref(PREF_UPDATE_REMINDER, jsonSet);
    }

    @DebugLog
    public Set<UpdateReminderPref> getUpdateReminderPref() {
        Set<UpdateReminderPref> preferences = new HashSet<>();
        Set<String> storedPrefs = getStringSetPref(PREF_UPDATE_REMINDER);
        if (storedPrefs != null) {
            for (String json : storedPrefs) {
                preferences.add(gson.fromJson(json, UpdateReminderPref.class));
            }
        }
        return preferences;
    }

    public void removeUpdateReminderPref() {
        removePref(PREF_UPDATE_REMINDER);
    }

    public boolean hasPreference(String key) {
        return sharedPrefs.contains(key);
    }

    public void setDefaultUpdateReminderHasBeenCreated() {
        setBooleanPref(DEFAULT_UPDATE_REMINDER_CREATED, true);
    }

    public boolean defaultUpdateReminderHasBeenCreated() {
        return getBooleanPref(DEFAULT_UPDATE_REMINDER_CREATED);
    }

    @Units.Interface
    public int getUnitTypePref() {
        switch (getIntegerPref(UNIT_TYPE)) {
            case Units.METRIC:
                return Units.METRIC;
            default:
            case Units.IMPERIAL:
                return Units.IMPERIAL;
        }
    }

    public void toggleUnitType() {
        setIntegerPref(UNIT_TYPE, getUnitTypePref() == Units.METRIC ? Units.IMPERIAL : Units.METRIC);
    }
}