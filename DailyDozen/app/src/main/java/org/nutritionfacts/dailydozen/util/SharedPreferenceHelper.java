package org.nutritionfacts.dailydozen.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chankruse on 15-09-02.
 */
public class SharedPreferenceHelper  {

    private static final String KEY_STORE_NAME = "SharedPreferenceHelper.KEY_STORE_NAME";
    private static final String KEY_ACTIVE_USER_ID = "SharedPreferenceHelper.KEY_ACTIVE_USER_ID";

    private SharedPreferences sharedPref;
    private Context context; //Use application context

    private static SharedPreferenceHelper ourInstance;

    public static synchronized void initializeInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new SharedPreferenceHelper(context);
        }
    }

    public static SharedPreferenceHelper getInstance() {
        return ourInstance;
    }

    private SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPref = this.context.getSharedPreferences(KEY_STORE_NAME, Context.MODE_PRIVATE);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getActiveUserId() {
        return sharedPref.getString(KEY_ACTIVE_USER_ID, null);
    }

    public void setActiveUserId(String activeUserId) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_ACTIVE_USER_ID, activeUserId);
        editor.commit();
    }
}