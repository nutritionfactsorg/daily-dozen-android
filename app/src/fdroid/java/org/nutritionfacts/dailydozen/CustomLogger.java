package org.nutritionfacts.dailydozen;

import android.content.Context;
import timber.log.Timber;

public class CustomLogger {
    public static void init(Context context) {
        Timber.plant(new Timber.DebugTree());
    }
}
