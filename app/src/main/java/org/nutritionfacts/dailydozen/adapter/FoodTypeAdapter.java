package org.nutritionfacts.dailydozen.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.databinding.FoodTypeItemBinding;

import java.util.List;

public class FoodTypeAdapter extends RecyclerView.Adapter<FoodTypeAdapter.ViewHolder> {
    private final List<String> foodTypes;
    private final List<String> foodVideos;

    public FoodTypeAdapter(List<String> typesOfFood, List<String> foodVideosLink) {
        this.foodTypes = typesOfFood;
        this.foodVideos = foodVideosLink;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FoodTypeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        if (foodVideos != null && !foodVideos.isEmpty() && position < foodVideos.size()) {
            final String link = foodVideos.get(position);

            if (!TextUtils.isEmpty(link)) {
                textView.setVisibility(View.VISIBLE);

                textView.setOnClickListener(v -> Common.openUrlInExternalBrowser(textView.getContext(), link));
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
        TextView foodName;
        TextView foodVideos;

        ViewHolder(FoodTypeItemBinding binding) {
            super(binding.getRoot());
            foodName = binding.foodName;
            foodVideos = binding.foodVideos;
        }
    }
}
