package org.nutritionfacts.dailydozen.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.nutritionfacts.dailydozen.adapter.DailyReminderAdapter;
import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.databinding.ActivityNotificationSettingsBinding;
import org.nutritionfacts.dailydozen.event.ReminderRemovedEvent;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.util.DateUtil;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

public class DailyReminderSettingsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private ActivityNotificationSettingsBinding binding;

    protected DailyReminderAdapter reminderAdapter;

    private UpdateReminderPref updateReminderPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        onDailyReminderSwitchToggled();
        onAddReminderClicked();

        updateReminderPref = Prefs.getInstance(this).getUpdateReminderPref();

        if (updateReminderPref != null) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    private void initUpdateReminderPrefConfig() {
        binding.dailyReminderSwitch.setChecked(true);
        toggleReminderViews();

        if (updateReminderPref == null) {
            updateReminderPref = new UpdateReminderPref();
        }

        reminderAdapter = new DailyReminderAdapter(this, updateReminderPref.getReminderTimes());
        binding.reminderTimesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.reminderTimesRecyclerView.setAdapter(reminderAdapter);

        setUpdateReminder();
    }

    private void setUpdateReminder() {
        Prefs.getInstance(this).setUpdateReminderPref(updateReminderPref);

        NotificationUtil.setAlarmForUpdateReminderNotification(this, updateReminderPref);
    }

    private void disableUpdateReminderPref() {
        binding.dailyReminderSwitch.setChecked(false);
        toggleReminderViews();

        NotificationUtil.cancelAlarmForUpdateReminderNotification(this, updateReminderPref);

        Prefs.getInstance(this).removeUpdateReminderPref();
    }

    public void onDailyReminderSwitchToggled() {
        binding.dailyReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                initUpdateReminderPrefConfig();
            } else {
                disableUpdateReminderPref();
            }
        });
    }

    private void toggleReminderViews() {
        binding.reminderTimesContainer.setVisibility(binding.dailyReminderSwitch.isChecked() ? View.VISIBLE : View.GONE);
        binding.addReminderButton.setVisibility(binding.dailyReminderSwitch.isChecked() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        updateReminderPref.setHourOfDay(hourOfDay);
        updateReminderPref.setMinute(minute);

        updateReminderPref.addReminderTime(getApplicationContext(), hourOfDay, minute);

        initUpdateReminderPrefConfig();
    }

    public void onAddReminderClicked() {
        binding.addReminderButton.setOnClickListener(v -> new TimePickerDialog(
                DailyReminderSettingsActivity.this,
                DailyReminderSettingsActivity.this,
                updateReminderPref.getHourOfDay(),
                updateReminderPref.getMinute(),
                DateUtil.is24HourTimeFormat(getApplicationContext()))
                .show());
    }

    @Subscribe
    public void onEvent(ReminderRemovedEvent event) {
        updateReminderPref.deleteReminderTime(event.getAdapterPosition());
        reminderAdapter.setReminders(updateReminderPref.getReminderTimes());

        if (updateReminderPref.getReminderTimes().isEmpty()) {
            // Disable the notification if the last reminder time has been removed
            disableUpdateReminderPref();
        } else {
            setUpdateReminder();
        }
    }
}
