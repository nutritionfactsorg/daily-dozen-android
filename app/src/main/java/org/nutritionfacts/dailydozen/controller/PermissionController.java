package org.nutritionfacts.dailydozen.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionController {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;

    public static boolean canWriteExternalStorage(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void askForWriteExternalStorage(final Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE_REQUEST);
    }

    public static boolean grantedWriteExternalStorage(int requestCode, int[] grantResults) {
        boolean permissionGranted = false;

        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }

        return permissionGranted;
    }
}
