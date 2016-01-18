package org.slavick.dailydozen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Day;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.widget.ServingsBarChartItem;

import java.util.List;

public class ServingsHistoryAdapter extends RecyclerView.Adapter<ServingsBarChartItem> {
    final private List<Day> dates;

    public ServingsHistoryAdapter() {
        this.dates = Day.getAllDays();
    }

    @Override
    public ServingsBarChartItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServingsBarChartItem(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servings_bar_chart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ServingsBarChartItem holder, int position) {
        holder.setServings(Servings.getTotalServingsOnDate(dates.get(position)));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}
