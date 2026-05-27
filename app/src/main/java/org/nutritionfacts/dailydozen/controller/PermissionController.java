package org.nutritionfacts.dailydozen.controller;

import android.app.Activity;
import android.app.NotificationChannel;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import org.nutritionfacts.dailydozen.util.NotificationUtil;

import timber.log.Timber;

public class PermissionController {
    public static boolean canPostNotifications(final Activity activity) {
        boolean areNotificationsEnabled = NotificationManagerCompat.from(activity).areNotificationsEnabled();
        Timber.d("NotificationManager.areNotificationsEnabled() = %s", areNotificationsEnabled);
        if (areNotificationsEnabled) {
            NotificationChannel channel = NotificationManagerCompat.from(activity).getNotificationChannel(NotificationUtil.CHANNEL_REMINDERS);
            if (channel != null) {
                Timber.d("channel.getImportance() = %s", channel.getImportance());
                return channel.getImportance() != NotificationManagerCompat.IMPORTANCE_NONE;
            }
        }
        return false;
    }

    public static boolean isPostNotificationsPermissionRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }
}
