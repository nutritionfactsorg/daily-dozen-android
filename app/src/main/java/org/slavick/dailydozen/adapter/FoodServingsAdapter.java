package org.slavick.dailydozen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.widget.FoodServings;

import java.util.Date;
import java.util.List;

public class FoodServingsAdapter extends RecyclerView.Adapter<FoodServings> {
    final private Date date;
    final private List<Food> foods;

    public FoodServingsAdapter(Date date) {
        this.date = date;
        this.foods = Food.getAllFoods();
    }

    @Override
    public FoodServings onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodServings(view);
    }

    @Override
    public void onBindViewHolder(FoodServings holder, int position) {
        holder.setDateAndFood(date, foods.get(position));
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}
