package org.nutritionfacts.dailydozen.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DailyReminderSettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    @BindView(R.id.daily_reminder_switch)
    protected SwitchCompat dailyReminderSwitch;
    @BindView(R.id.daily_reminder_config_container)
    protected ViewGroup vgDailyReminderConfig;
    @BindView(R.id.daily_reminder_time)
    protected Button tvTime;
    @BindView(R.id.daily_reminder_vibrate_container)
    protected ViewGroup vgVibrate;
    @BindView(R.id.daily_reminder_vibrate)
    protected SwitchCompat vibrateSwitch;
    @BindView(R.id.daily_reminder_play_sound)
    protected SwitchCompat playSoundSwitch;

    private UpdateReminderPref updateReminderPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        updateReminderPref = Prefs.getInstance(this).getUpdateReminderPref();

        if (updateReminderPref != null) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    private void initUpdateReminderPrefConfig() {
        dailyReminderSwitch.setChecked(true);
        vgDailyReminderConfig.setVisibility(View.VISIBLE);

        if (updateReminderPref == null) {
            updateReminderPref = new UpdateReminderPref();
        }

        tvTime.setText(updateReminderPref.toString());

        initVibratePref();

        playSoundSwitch.setChecked(updateReminderPref.isPlaySound());

        setUpdateReminder();
    }

    private void initVibratePref() {
        vgVibrate.setVisibility(NotificationUtil.deviceHasVibrator(this) ? View.VISIBLE : View.GONE);
        vibrateSwitch.setChecked(updateReminderPref.isVibrate());
    }

    private void setUpdateReminder() {
        Prefs.getInstance(this).setUpdateReminderPref(updateReminderPref);

        NotificationUtil.setAlarmForUpdateReminderNotification(this, updateReminderPref);
    }

    private void disableUpdateReminderPref() {
        dailyReminderSwitch.setChecked(false);
        vgDailyReminderConfig.setVisibility(View.GONE);

        NotificationUtil.cancelAlarmForUpdateReminderNotification(this, updateReminderPref);

        Prefs.getInstance(this).removeUpdateReminderPref();
    }

    @OnClick({R.id.daily_reminder_set_time, R.id.daily_reminder_time})
    public void onUpdateReminderSetTimeClicked() {
        new TimePickerDialog(
                DailyReminderSettingsActivity.this,
                DailyReminderSettingsActivity.this,
                updateReminderPref.getHourOfDay(),
                updateReminderPref.getMinute(),
                false)
                .show();
    }

    @OnCheckedChanged(R.id.daily_reminder_switch)
    public void onDailyReminderSwitchToggled(final boolean isChecked) {
        vgDailyReminderConfig.setVisibility(isChecked ? View.VISIBLE : View.GONE);

        if (isChecked) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    @OnCheckedChanged(R.id.daily_reminder_vibrate)
    public void onVibrateSwitchToggled(final boolean isChecked) {
        updateReminderPref.setVibrate(isChecked);

        initUpdateReminderPrefConfig();
    }

    @OnCheckedChanged(R.id.daily_reminder_play_sound)
    public void onPlaySoundSwitchToggled(final boolean isChecked) {
        updateReminderPref.setPlaySound(isChecked);

        initUpdateReminderPrefConfig();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateReminderPref.setHourOfDay(hourOfDay);
        updateReminderPref.setMinute(minute);

        initUpdateReminderPrefConfig();
    }
}
