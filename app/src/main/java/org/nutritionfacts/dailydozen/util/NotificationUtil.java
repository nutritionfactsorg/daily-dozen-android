package org.nutritionfacts.dailydozen.util;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.MainActivity;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.receiver.AlarmReceiver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import timber.log.Timber;

public class NotificationUtil {
    private static final int NOTIFICATION_SETTINGS_ID = 0;
    private static int UPDATE_REMINDER_ID = 1;
    private static final Gson gson = new Gson();

    public static void showUpdateReminderNotification(final Context context, Intent intent) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(getRandomNotificationIcon(context))
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_text))
                .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.daily_reminder_settings), getNotificationSettingsClickedIntent(context));

        if (intent != null && intent.getExtras() != null) {
            try {
                final String updateReminderPrefJson = intent.getExtras().getString(Args.UPDATE_REMINDER_PREF);

                if (!TextUtils.isEmpty(updateReminderPrefJson)) {
                    final UpdateReminderPref updateReminderPref = gson.fromJson(updateReminderPrefJson, UpdateReminderPref.class);

                    if (updateReminderPref.isVibrate()) {
                        final Vibrator vibratorService = getVibratorService(context);

                        if (vibratorService.hasVibrator()) {
                            vibratorService.vibrate(150);
                        }
                    }

                    if (updateReminderPref.isPlaySound()) {
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    }

                    int updateReminderId = updateReminderPref.getId();

                    builder.setContentIntent(getUpdateReminderClickedIntent(context, updateReminderId));

                    setAlarmForUpdateReminderNotification(context, updateReminderPref);
                    getNotificationManager(context).notify(updateReminderId, builder.build());
                }
            } catch (RuntimeException e) {
                Timber.e("Caught RuntimeException in showUpdateReminderNotification", e);
            }
        }
    }

    public static void dismissUpdateReminderNotifications(final Context context) {
        getNotificationManager(context).cancelAll();
    }

    private static AlarmManager getAlarmManager(final Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static NotificationManager getNotificationManager(final Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static Vibrator getVibratorService(final Context context) {
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static boolean deviceHasVibrator(final Context context) {
        return getVibratorService(context).hasVibrator();
    }

    private static PendingIntent getUpdateReminderClickedIntent(final Context context, int requestCode) {
        return TaskStackBuilder.create(context)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
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

    public static void setAlarmForUpdateReminderNotification(final Context context, final UpdateReminderPref pref) {
        if (pref != null) {
            final AlarmManager alarmManager = getAlarmManager(context);

            final PendingIntent alarmPendingIntent = getAlarmPendingIntent(context, pref);

            alarmManager.cancel(alarmPendingIntent);

            final long alarmTimeInMillis = pref.getAlarmTimeInMillis();
            Timber.d(String.format("setAlarmForUpdateReminderNotification: %s", alarmTimeInMillis));

            setAlarm(alarmManager, alarmPendingIntent, alarmTimeInMillis);
        }
    }

    private static void setAlarm(AlarmManager alarmManager, PendingIntent alarmPendingIntent, long alarmTimeInMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, alarmPendingIntent);
        } else {
            // Note: Beginning in API 19, the trigger time passed to this method is treated as inexact: the alarm
            // will not be delivered before this time, but may be deferred and delivered some time later
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, alarmPendingIntent);
        }
    }

    public static void cancelAlarmForUpdateReminderNotification(final Context context, UpdateReminderPref pref) {
        Timber.d("cancelAlarmForUpdateReminderNotification");
        getAlarmManager(context).cancel(getAlarmPendingIntent(context, pref));
    }

    private static PendingIntent getAlarmPendingIntent(Context context, UpdateReminderPref pref) {
        final Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = null;

        if (pref != null) {
            intent.putExtra(Args.UPDATE_REMINDER_PREF, gson.toJson(pref));
            pendingIntent = PendingIntent.getBroadcast(context, pref.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    private static void initUpdateReminderId(Set<UpdateReminderPref> updateReminderPrefSet) {
        if (updateReminderPrefSet.isEmpty())
            return;

        Iterator<UpdateReminderPref> it = updateReminderPrefSet.iterator();
        int max = 0;

        while (it.hasNext()) {
            UpdateReminderPref updateReminderPref = it.next();
            int id = updateReminderPref.getId();

            if (max < id)
                max = id;
        }

        UPDATE_REMINDER_ID = max + 1;
    }

    public static void initUpdateReminderNotificationAlarm(final Context context) {
        final Prefs prefs = Prefs.getInstance(context);

        Set<UpdateReminderPref> updateReminderPref = prefs.getUpdateReminderPref();

        if (updateReminderPref.isEmpty() && !prefs.defaultUpdateReminderHasBeenCreated()) {
            Timber.d("initUpdateReminderNotificationAlarm: Creating default update reminder");

            updateReminderPref = new HashSet<>();
            prefs.setUpdateReminderPref(updateReminderPref);

            prefs.setDefaultUpdateReminderHasBeenCreated();
        }

        boolean updatePreferences = false;

        initUpdateReminderId(updateReminderPref);

        for (UpdateReminderPref pref: updateReminderPref) {
            // Set an id for the update reminder that was previously saved in preferences but didn't have an ID
            if (pref.getId() == UpdateReminderPref.DEFAULT_ID) {
                updatePreferences = true;
                pref.setId(getNextAvailableNotificationId());
            }

            cancelAlarmForUpdateReminderNotification(context, pref);
            setAlarmForUpdateReminderNotification(context, pref);
        }

        if (updatePreferences) {
            Prefs.getInstance(context).setUpdateReminderPref(updateReminderPref);
        }
    }

    public static int getNextAvailableNotificationId() {
        return UPDATE_REMINDER_ID++;
    }
}
