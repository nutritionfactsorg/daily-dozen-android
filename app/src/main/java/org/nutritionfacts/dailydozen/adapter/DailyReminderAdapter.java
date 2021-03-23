package org.nutritionfacts.dailydozen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Bus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class DailyReminderAdapter extends RecyclerView.Adapter<DailyReminderAdapter.ViewHolder> {
    private Context context;
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
        if (reminderTimes.isEmpty()) {
            return;
        }

        holder.setTime(reminderTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return reminderTimes.size();
    }

    public void setReminders(List<String> reminderTimes) {
        this.reminderTimes = reminderTimes;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.reminder_time)
        TextView tvTime;

        public void setTime(String time) {
            tvTime.setText(time);
        }

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick(R.id.reminder_delete)
        public void onDeleteReminderClicked() {
            Bus.reminderRemovedEvent(getAdapterPosition());
        }
    }
}
