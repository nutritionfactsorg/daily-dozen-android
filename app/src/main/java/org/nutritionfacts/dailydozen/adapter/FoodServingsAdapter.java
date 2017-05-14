package org.nutritionfacts.dailydozen.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodServingsAdapter extends RecyclerView.Adapter<FoodServingsAdapter.ViewHolder> {
    private List<String> servingSizes;

    public FoodServingsAdapter(List<String> servingSizes) {
        this.servingSizes = servingSizes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String servingSize = servingSizes.get(position);

        if (!TextUtils.isEmpty(servingSize)) {
            setBackgroundColor(holder, position);
            setServingSize(holder.servingSize, servingSize);
        }
    }

    private void setBackgroundColor(ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(Common.getListItemColorForPosition(holder.itemView.getContext(), position));
    }

    private void setServingSize(TextView textView, String servingSize) {
        textView.setText(servingSize);
    }

    @Override
    public int getItemCount() {
        return servingSizes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(android.R.id.text1)
        TextView servingSize;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
