package org.nutritionfacts.dailydozen.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.MainActivity;
import org.nutritionfacts.dailydozen.model.FoodInfo;

import java.util.Random;

public class NotificationUtil {
    private static final int UPDATE_REMINDER_ID = 1;

    public static void showUpdateReminderNotification(final Context context) {
        final PendingIntent onNotificationClickedIntent = TaskStackBuilder.create(context)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(UPDATE_REMINDER_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(getRandomNotificationIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(onNotificationClickedIntent);

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private static int getRandomNotificationIcon(final Context context) {
        // Only certain icons look good as notification icons, so we randomly choose between those.
        final String[] foodNames = context.getResources().getStringArray(R.array.notification_icon_food_names);

        return FoodInfo.getFoodIcon(foodNames[new Random().nextInt(foodNames.length)]);
    }
}
