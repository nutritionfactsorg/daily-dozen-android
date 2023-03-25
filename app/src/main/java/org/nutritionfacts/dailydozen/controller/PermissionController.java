package org.nutritionfacts.dailydozen.controller;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.nutritionfacts.dailydozen.util.NotificationUtil;

import timber.log.Timber;

public class PermissionController {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;
    private static final int POST_NOTIFICATIONS_REQUEST = 2;

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

    public static boolean canWriteExternalStorage(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void askForPostNotifications(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    POST_NOTIFICATIONS_REQUEST);
        }
    }

    public static void askForWriteExternalStorage(final Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE_REQUEST);
    }

    public static boolean grantedPostNotifications(int requestCode, int[] grantResults) {
        return requestCode == POST_NOTIFICATIONS_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean grantedWriteExternalStorage(int requestCode, int[] grantResults) {
        return requestCode == WRITE_EXTERNAL_STORAGE_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
