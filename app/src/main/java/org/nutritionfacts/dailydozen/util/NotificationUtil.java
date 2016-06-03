package org.nutritionfacts.dailydozen.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.MainActivity;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.receiver.AlarmReceiver;

import java.util.Random;

public class NotificationUtil {
    private static final int UPDATE_REMINDER_ID = 1;
    private static final int NOTIFICATION_SETTINGS_ID = 2;

    public static void showUpdateReminderNotification(final Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(getRandomNotificationIcon(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(getUpdateReminderClickedIntent(context))
                .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.notification_settings), getNotificationSettingsClickedIntent(context));

        getNotificationManager(context).notify(UPDATE_REMINDER_ID, builder.build());
    }

    public static void dismissUpdateReminderNotification(final Context context) {
        getNotificationManager(context).cancel(UPDATE_REMINDER_ID);
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static PendingIntent getUpdateReminderClickedIntent(final Context context) {
        return TaskStackBuilder.create(context)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(UPDATE_REMINDER_ID, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getNotificationSettingsClickedIntent(final Context context) {
        final Intent openNotificationSettingsIntent = new Intent(context, MainActivity.class);
        openNotificationSettingsIntent.putExtra(Args.OPEN_NOTIFICATION_SETTINGS, true);

        return TaskStackBuilder.create(context)
                .addNextIntent(openNotificationSettingsIntent)
                .getPendingIntent(NOTIFICATION_SETTINGS_ID, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static int getRandomNotificationIcon(final Context context) {
        // Only certain icons look good as notification icons, so we randomly choose between those.
        final String[] foodNames = context.getResources().getStringArray(R.array.notification_icon_food_names);

        return FoodInfo.getFoodIcon(foodNames[new Random().nextInt(foodNames.length)]);
    }

    public static void setRepeatingAlarmForNotification(final Context context, final UpdateReminderPref pref) {
        if (pref != null) {
            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            final PendingIntent alarmPendingIntent = getAlarmPendingIntent(context);

            alarmManager.cancel(alarmPendingIntent);

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    pref.getAlarmTimeInMillis(),
                    DateUtil.MILLIS_PER_DAY,
                    alarmPendingIntent);
        }
    }

    public static void cancelRepeatingAlarmForNotification(final Context context) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(
                getAlarmPendingIntent(context));
    }

    private static PendingIntent getAlarmPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void initUpdateNotificationAlarm(final Context context) {
        cancelRepeatingAlarmForNotification(context);
        setRepeatingAlarmForNotification(context, Prefs.getInstance(context).getUpdateReminderPref());
    }
}
