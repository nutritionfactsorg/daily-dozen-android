package org.nutritionfacts.dailydozen.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodTypeAdapter extends RecyclerView.Adapter<FoodTypeAdapter.ViewHolder> {
    private final List<String> foodTypes;
    private final List<String> foodVideos;

    public FoodTypeAdapter(List<String> typesOfFood, List<String> foodVideosLink) {
        this.foodTypes = typesOfFood;
        this.foodVideos = foodVideosLink;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_type_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String foodName = foodTypes.get(position);

        if (!TextUtils.isEmpty(foodName)) {
            setBackgroundColor(holder, position);
            setFoodName(holder.foodName, foodName);
            setFoodVideosLink(holder.foodVideos, position);
        }
    }

    private void setBackgroundColor(ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(Common.getListItemColorForPosition(holder.itemView.getContext(), position));
    }

    private void setFoodName(TextView textView, String foodName) {
        textView.setText(foodName);
    }

    private void setFoodVideosLink(final TextView textView, int position) {
        if (foodVideos != null && !foodVideos.isEmpty()) {
            final String link = foodVideos.get(position);

            if (!TextUtils.isEmpty(link)) {
                textView.setVisibility(View.VISIBLE);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Common.openUrlInExternalBrowser(textView.getContext(), link);
                    }
                });
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return foodTypes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.food_name)
        TextView foodName;
        @BindView(R.id.food_videos)
        TextView foodVideos;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
