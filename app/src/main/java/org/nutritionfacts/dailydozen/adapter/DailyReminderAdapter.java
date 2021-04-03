package org.nutritionfacts.dailydozen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.nutritionfacts.dailydozen.R;
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
        View root = LayoutInflater.from(context).inflate(R.layout.reminder_time, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (reminderTimes == null || reminderTimes.isEmpty()) {
            return;
        }

        holder.setTime(reminderTimes.get(position));
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
        private ReminderTimeBinding binding;

        public void setTime(String time) {
            binding.reminderTime.setText(time);
        }

        ViewHolder(View itemView) {
            super(itemView);
            binding = ReminderTimeBinding.inflate(LayoutInflater.from(itemView.getContext()));
            onDeleteReminderClicked();
        }

        public void onDeleteReminderClicked() {
            binding.reminderDelete.setOnClickListener(v -> Bus.reminderRemovedEvent(getAdapterPosition()));
        }
    }
}
