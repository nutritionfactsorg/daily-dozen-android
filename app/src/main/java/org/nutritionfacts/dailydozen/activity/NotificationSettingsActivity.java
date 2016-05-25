package org.nutritionfacts.dailydozen.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class NotificationSettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    @BindView(R.id.daily_reminder_switch)
    protected SwitchCompat dailyReminderSwitch;
    @BindView(R.id.daily_reminder_config_container)
    protected ViewGroup vgDailyReminderConfig;
    @BindView(R.id.daily_reminder_time)
    protected Button tvTime;

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

        setUpdateReminder();
    }

    private void setUpdateReminder() {
        Prefs.getInstance(this).setUpdateReminderPref(updateReminderPref);

        NotificationUtil.setRepeatingAlarmForNotification(this, updateReminderPref);
    }

    private void disableUpdateReminderPref() {
        dailyReminderSwitch.setChecked(false);
        vgDailyReminderConfig.setVisibility(View.GONE);

        Prefs.getInstance(this).removeUpdateReminderPref();

        NotificationUtil.cancelRepeatingAlarmForNotification(this);
    }

    @OnClick({R.id.daily_reminder_set_time, R.id.daily_reminder_time})
    public void onUpdateReminderSetTimeClicked() {
        new TimePickerDialog(
                NotificationSettingsActivity.this,
                NotificationSettingsActivity.this,
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateReminderPref.setHourOfDay(hourOfDay);
        updateReminderPref.setMinute(minute);

        // Call this to update the preference
        initUpdateReminderPrefConfig();
    }
}
