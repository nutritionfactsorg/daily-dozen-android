package org.nutritionfacts.dailydozen.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.delegate.DailyReminderDelegate;
import org.nutritionfacts.dailydozen.model.pref.UpdateReminderPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Optional;

public class DailyReminderAdapter extends RecyclerView.Adapter<DailyReminderAdapter.ViewHolder> {
    private Context context;
    private DailyReminderDelegate delegate;
    private List<UpdateReminderPref> updateReminderPrefList;
    private Map<SwitchCompat, UpdateReminderPref> vibratePrefMap;
    private Map<SwitchCompat, UpdateReminderPref> soundPrefMap;
    private Map<TimePickerDialog, UpdateReminderPref> timePickerPrefMap;
    private Map<Button, UpdateReminderPref> timeButtonPrefMap;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private TimePickerDialog currentTimePickerDialog;

    public DailyReminderAdapter(Context context, Set<UpdateReminderPref> updateReminderPrefSet, final DailyReminderDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
        updateReminderPrefList = new ArrayList<>(updateReminderPrefSet);
        vibratePrefMap = new HashMap<>();
        soundPrefMap = new HashMap<>();
        timeButtonPrefMap = new HashMap<>();
        timePickerPrefMap = new HashMap<>();
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                timePickerPrefMap.get(currentTimePickerDialog).setHourOfDay(hourOfDay);
                timePickerPrefMap.get(currentTimePickerDialog).setMinute(minute);
                delegate.initUpdateReminderPrefConfig();
                delegate.updateReminders();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.daily_reminder_card, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UpdateReminderPref pref = updateReminderPrefList.get(position);
        holder.tvTime.setText(pref.toString());
        holder.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        context,
                        timeSetListener,
                        timeButtonPrefMap.get(view).getHourOfDay(),
                        timeButtonPrefMap.get(view).getMinute(),
                        false);
                timePickerPrefMap.put(timePickerDialog, pref);
                currentTimePickerDialog = timePickerDialog;
                timePickerDialog.show();
            }
        });
        mapViewsToPref(holder, pref);
        markChecks(holder, pref);

    }

    private void mapViewsToPref(ViewHolder holder, UpdateReminderPref pref) {
        vibratePrefMap.put(holder.vibrateSwitch, pref);
        soundPrefMap.put(holder.playSoundSwitch, pref);
        timeButtonPrefMap.put(holder.tvTime, pref);
    }

    private void markChecks(ViewHolder holder, UpdateReminderPref pref) {
        
        if (pref.isPlaySound()) {
            holder.playSoundSwitch.setChecked(true);
        } else {
            holder.playSoundSwitch.setChecked(false);
        }
        
        if (pref.isVibrate()) {
            holder.vibrateSwitch.setChecked(true);
        } else {
            holder.vibrateSwitch.setChecked(false);
        }
    }
    
    @Override
    public int getItemCount() {
        if (updateReminderPrefList.size() > 0){
            return updateReminderPrefList.size();
        } else {
            return 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.daily_reminder_config_container)
        ViewGroup vgDailyReminderConfig;

        @Nullable
        @BindView(R.id.daily_reminder_time)
        Button tvTime;

        @Nullable
        @BindView(R.id.daily_reminder_vibrate_container)
        ViewGroup vgVibrate;

        @Nullable
        @BindView(R.id.daily_reminder_vibrate)
        SwitchCompat vibrateSwitch;

        @Nullable
        @BindView(R.id.daily_reminder_play_sound)
        SwitchCompat playSoundSwitch;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnCheckedChanged(R.id.daily_reminder_vibrate)
        public void onVibrateSwitchToggled(SwitchCompat vibrateSwitch, final boolean isChecked) {
            vibratePrefMap.get(vibrateSwitch).setVibrate(isChecked);
            delegate.initUpdateReminderPrefConfig();
        }

        @Optional
        @OnCheckedChanged(R.id.daily_reminder_play_sound)
        public void onPlaySoundSwitchToggled(SwitchCompat playSoundSwitch, final boolean isChecked) {
            soundPrefMap.get(playSoundSwitch).setPlaySound(isChecked);

            delegate.initUpdateReminderPrefConfig();
        }
    }

}
