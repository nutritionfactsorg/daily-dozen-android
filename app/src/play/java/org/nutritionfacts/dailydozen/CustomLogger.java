package org.nutritionfacts.dailydozen;

import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class CustomLogger {
    public static void init(Context context) {
        Fabric.with(context, new Crashlytics());
    }
}
