package org.nutritionfacts.dailydozen.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.gson.Gson;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.MainActivity;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.receiver.AlarmReceiver;

import timber.log.Timber;

public class NotificationUtil {
    private static final int UPDATE_REMINDER_ID = 1;
    private static final int NOTIFICATION_SETTINGS_ID = 2;

    private static final String CHANNEL_REMINDERS = "DAILY_DOZEN_REMINDERS_CHANNEL";

    public static void showUpdateReminderNotification(final Context context, Intent intent) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_REMINDERS)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_text))
                .setContentIntent(getUpdateReminderClickedIntent(context))
                .addAction(R.drawable.ic_settings_black_24dp, context.getString(R.string.daily_reminder_settings), getNotificationSettingsClickedIntent(context));

        if (intent != null && intent.getExtras() != null) {
            try {
                final String updateReminderPrefJson = intent.getExtras().getString(Args.UPDATE_REMINDER_PREF);

                if (!TextUtils.isEmpty(updateReminderPrefJson)) {
                    final UpdateReminderPref updateReminderPref = new Gson().fromJson(updateReminderPrefJson, UpdateReminderPref.class);

                    if (updateReminderPref.isVibrate()) {
                        final Vibrator vibratorService = getVibratorService(context);

                        if (vibratorService.hasVibrator()) {
                            vibratorService.vibrate(150);
                        }
                    }

                    if (updateReminderPref.isPlaySound()) {
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    }

                    setAlarmForUpdateReminderNotification(context, updateReminderPref);
                }
            } catch (RuntimeException e) {
                Timber.e(e, "Caught RuntimeException in showUpdateReminderNotification");
            }
        }

        getNotificationManager(context).notify(UPDATE_REMINDER_ID, builder.build());
    }

    public static void dismissUpdateReminderNotification(final Context context) {
        getNotificationManager(context).cancel(UPDATE_REMINDER_ID);
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

    public static void setAlarmForUpdateReminderNotification(final Context context, final UpdateReminderPref pref) {
        if (pref != null) {
            final AlarmManager alarmManager = getAlarmManager(context);

            final PendingIntent alarmPendingIntent = getAlarmPendingIntent(context, pref);

            alarmManager.cancel(alarmPendingIntent);

            final long alarmTimeInMillis = pref.getNextAlarmTimeInMillis(context);
            Timber.d("setAlarmForUpdateReminderNotification: %s", alarmTimeInMillis);

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

        if (pref != null) {
            intent.putExtra(Args.UPDATE_REMINDER_PREF, new Gson().toJson(pref));
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void init(final Context context) {
        initNotificationChannels(context);
        initUpdateReminderNotificationAlarm(context);
    }

    private static void initNotificationChannels(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationManager(context).createNotificationChannel(new NotificationChannel(
                    CHANNEL_REMINDERS,
                    context.getString(R.string.channel_reminders_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            ));
        }
    }

    private static void initUpdateReminderNotificationAlarm(final Context context) {
        final Prefs prefs = Prefs.getInstance(context);

        UpdateReminderPref updateReminderPref = prefs.getUpdateReminderPref();

        if (updateReminderPref == null && !prefs.defaultUpdateReminderHasBeenCreated()) {
            Timber.d("initUpdateReminderNotificationAlarm: Creating default update reminder");

            updateReminderPref = new UpdateReminderPref();
            prefs.setUpdateReminderPref(updateReminderPref);

            prefs.setDefaultUpdateReminderHasBeenCreated();
        }

        cancelAlarmForUpdateReminderNotification(context, updateReminderPref);
        setAlarmForUpdateReminderNotification(context, updateReminderPref);
    }
}
