package org.nutritionfacts.dailydozen.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.DailyReminderAdapter;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.event.ReminderRemovedEvent;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DailyReminderSettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    @BindView(R.id.daily_reminder_switch)
    protected SwitchCompat dailyReminderSwitch;
    @BindView(R.id.sound_and_vibration_container)
    protected ViewGroup vgSoundAndVibration;
    @BindView(R.id.reminder_times_container)
    protected ViewGroup vgReminderTimes;
    @BindView(R.id.daily_reminder_vibrate_container)
    protected ViewGroup vgVibrate;
    @BindView(R.id.daily_reminder_vibrate)
    protected SwitchCompat vibrateSwitch;
    @BindView(R.id.daily_reminder_play_sound)
    protected SwitchCompat playSoundSwitch;
    @BindView(R.id.reminder_times_recycler_view)
    protected RecyclerView reminderTimesRecyclerView;
    @BindView(R.id.add_reminder_button)
    protected FloatingActionButton btnAddReminder;

    protected DailyReminderAdapter reminderAdapter;

    private UpdateReminderPref updateReminderPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bus.unregister(this);
    }

    private void init() {
        updateReminderPref = Prefs.getInstance(this).getUpdateReminderPref();

        reminderAdapter = new DailyReminderAdapter(this, updateReminderPref.getReminderTimes());
        reminderTimesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderTimesRecyclerView.setAdapter(reminderAdapter);

        if (updateReminderPref != null) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    private void initUpdateReminderPrefConfig() {
        dailyReminderSwitch.setChecked(true);
        toggleReminderViews();

        if (updateReminderPref == null) {
            updateReminderPref = new UpdateReminderPref();
        }

        reminderAdapter.setReminders(updateReminderPref.getReminderTimes());

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
        toggleReminderViews();

        NotificationUtil.cancelAlarmForUpdateReminderNotification(this, updateReminderPref);

        Prefs.getInstance(this).removeUpdateReminderPref();
    }

    @OnCheckedChanged(R.id.daily_reminder_switch)
    public void onDailyReminderSwitchToggled(final boolean isChecked) {
        if (isChecked) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    private void toggleReminderViews() {
        vgSoundAndVibration.setVisibility(dailyReminderSwitch.isChecked() ? View.VISIBLE : View.GONE);
        vgReminderTimes.setVisibility(dailyReminderSwitch.isChecked() ? View.VISIBLE : View.GONE);
        btnAddReminder.setVisibility(dailyReminderSwitch.isChecked() ? View.VISIBLE : View.GONE);
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

        updateReminderPref.addReminderTime(hourOfDay, minute);

        initUpdateReminderPrefConfig();
    }

    @OnClick(R.id.add_reminder_button)
    public void onAddReminderClicked() {
        new TimePickerDialog(
                DailyReminderSettingsActivity.this,
                DailyReminderSettingsActivity.this,
                updateReminderPref.getHourOfDay(),
                updateReminderPref.getMinute(),
                false)
                .show();
    }

    @Subscribe
    public void onEvent(ReminderRemovedEvent event) {
        updateReminderPref.deleteReminderTime(event.getAdapterPosition());
        // TODO (slavick) cancel deleted reminder
        reminderAdapter.setReminders(updateReminderPref.getReminderTimes());
        setUpdateReminder();
    }
}
