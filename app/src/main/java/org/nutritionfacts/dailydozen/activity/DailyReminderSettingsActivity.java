package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.adapter.DailyReminderAdapter;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.delegate.DailyReminderDelegate;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;
import org.nutritionfacts.dailydozen.util.NotificationUtil;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class DailyReminderSettingsActivity extends AppCompatActivity implements DailyReminderDelegate {

    @BindView(R.id.daily_reminder_switch)
    protected SwitchCompat dailyReminderSwitch;

    @BindView(R.id.daily_reminder_recycler_view)
    protected RecyclerView reminderRecyclerView;

    protected DailyReminderAdapter reminderAdapter;

    private Set<UpdateReminderPref> updateReminderPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        updateReminderPrefs = Prefs.getInstance(this).getUpdateReminderPref();
        if (updateReminderPrefs == null || updateReminderPrefs.isEmpty()) {
            updateReminderPrefs = new HashSet<>();
            updateReminderPrefs.add(new UpdateReminderPref());
        }
        reminderAdapter = new DailyReminderAdapter(this, updateReminderPrefs, this);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderRecyclerView.setAdapter(reminderAdapter);

        if (updateReminderPrefs != null) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    @Override
    public void initUpdateReminderPrefConfig() {
        dailyReminderSwitch.setChecked(true);
        reminderRecyclerView.setVisibility(View.VISIBLE); //Needs to be recycler view

        if (updateReminderPrefs == null) {
            updateReminderPrefs = new HashSet<>();
            updateReminderPrefs.add(new UpdateReminderPref());
        }

        setUpdateReminder();
    }

    @Override
    public void updateReminders() {
        reminderAdapter = new DailyReminderAdapter(this, updateReminderPrefs, this);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reminderRecyclerView.setAdapter(reminderAdapter);
    }

    private void setUpdateReminder() {
        Prefs.getInstance(this).setUpdateReminderPref(updateReminderPrefs);
        for (UpdateReminderPref pref : updateReminderPrefs) {
            NotificationUtil.setAlarmForUpdateReminderNotification(this, pref);
        }
    }

    private void disableUpdateReminderPref() {
        dailyReminderSwitch.setChecked(false);
        for (UpdateReminderPref pref : updateReminderPrefs) {
            NotificationUtil.cancelAlarmForUpdateReminderNotification(this, pref);
        }
        Prefs.getInstance(this).removeUpdateReminderPref();
    }

    @OnCheckedChanged(R.id.daily_reminder_switch)
    public void onDailyReminderSwitchToggled(final boolean isChecked) {
        reminderRecyclerView.setVisibility(isChecked ? View.VISIBLE : View.GONE);

        if (isChecked) {
            initUpdateReminderPrefConfig();
        } else {
            disableUpdateReminderPref();
        }
    }

    @OnClick(R.id.addNewReminderBtn)
    public void addNewReminder() {
        updateReminderPrefs.add(new UpdateReminderPref());
        updateReminders();
    }
}

