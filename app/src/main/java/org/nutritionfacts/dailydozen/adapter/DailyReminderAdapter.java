package org.nutritionfacts.dailydozen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.nutritionfacts.dailydozen.controller.Bus;
import org.nutritionfacts.dailydozen.databinding.ReminderTimeBinding;

import java.util.List;

public class DailyReminderAdapter extends RecyclerView.Adapter<DailyReminderAdapter.ViewHolder> {
    final private Context context;
    private List<String> reminderTimes;

    public DailyReminderAdapter(Context context, List<String> reminderTimes) {
        this.context = context;
        this.reminderTimes = reminderTimes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ReminderTimeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (reminderTimes == null || reminderTimes.isEmpty()) {
            return;
        }

        holder.reminderTime.setText(reminderTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return reminderTimes != null ? reminderTimes.size() : 0;
    }

    public void setReminders(List<String> reminderTimes) {
        this.reminderTimes = reminderTimes;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTime;

        ViewHolder(ReminderTimeBinding binding) {
            super(binding.getRoot());
            reminderTime = binding.reminderTime;
            binding.reminderDelete.setOnClickListener(v -> Bus.reminderRemovedEvent(getAdapterPosition()));
        }
    }
}
