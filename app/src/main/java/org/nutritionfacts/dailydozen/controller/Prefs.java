package org.nutritionfacts.dailydozen.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.model.enums.Units;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;

import hugo.weaving.DebugLog;
import timber.log.Timber;

public class Prefs {
    private static final String APP_MODE_DAILY_DOZEN_ONLY = "app_mode_daily_dozen_only";
    private static final String STREAKS_HAVE_BEEN_CALCULATED_ON_DATABASE_UPGRADE_V2 = "v2_streaks_calculated";
    private static final String USER_HAS_SEEN_FIRST_STAR_EXPLOSION = "user_has_seen_first_star_explosion";
    private static final String USER_HAS_SEEN_ONBOARDING_SCREEN = "user_has_seen_onboarding_screen";
    private static final String PREF_SHOW_WEIGHT = "pref_show_weight";
    private static final String PREF_UPDATE_REMINDER = "pref_update_reminder";
    private static final String DEFAULT_UPDATE_REMINDER_CREATED = "default_update_reminder_created";
    private static final String UNIT_TYPE = "unit_type";

    private static Prefs instance;

    private SharedPreferences sharedPrefs;

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

    private void setStringPref(final String name, final String value) {
        sharedPrefs.edit().putString(name, value).apply();
    }

    private boolean getBooleanPref(final String name) {
        return sharedPrefs.getBoolean(name, false);
    }

    private void setBooleanPref(final String name, final boolean value) {
        Timber.d("setBooleanPref: [%s] = [%s]", name, value);
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

    public boolean userHasSeenOnboardingScreen() {
        return getBooleanPref(USER_HAS_SEEN_ONBOARDING_SCREEN);
    }

    public void setUserHasSeenOnboardingScreen() {
        setBooleanPref(USER_HAS_SEEN_ONBOARDING_SCREEN, true);
    }

    public boolean isAppModeDailyDozenOnly() {
        return sharedPrefs.getBoolean(Prefs.APP_MODE_DAILY_DOZEN_ONLY, false);
    }

    public void setAppModeToDailyDozenOnly() {
        setBooleanPref(Prefs.APP_MODE_DAILY_DOZEN_ONLY, true);
    }

    public void setAppModeToDailyDozenAndTweaks() {
        setBooleanPref(Prefs.APP_MODE_DAILY_DOZEN_ONLY, false);
    }

    @DebugLog
    public void setUpdateReminderPref(UpdateReminderPref pref) {
        setStringPref(PREF_UPDATE_REMINDER, new Gson().toJson(pref));
    }

    @DebugLog
    public UpdateReminderPref getUpdateReminderPref() {
        return new Gson().fromJson(getStringPref(PREF_UPDATE_REMINDER), UpdateReminderPref.class);
    }

    public void removeUpdateReminderPref() {
        removePref(PREF_UPDATE_REMINDER);
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

    public void toggleWeightVisibility() {
        setBooleanPref(PREF_SHOW_WEIGHT, !getWeightVisible());
    }

    public boolean getWeightVisible() {
        return sharedPrefs.getBoolean(PREF_SHOW_WEIGHT, true);
    }
}