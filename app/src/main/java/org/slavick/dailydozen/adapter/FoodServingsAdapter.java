package org.slavick.dailydozen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.widget.FoodServings;

import java.util.Date;
import java.util.List;

public class FoodServingsAdapter extends RecyclerView.Adapter<FoodServings> {
    final private FoodServings.ClickListener listener;
    final private Date date;
    final private List<Food> foods;

    public FoodServingsAdapter(FoodServings.ClickListener listener, Date date) {
        this.listener = listener;
        this.date = date;
        this.foods = Food.getAllFoods();
    }

    @Override
    public FoodServings onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodServings(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodServings holder, int position) {
        holder.setListener(listener);

        final Food food = foods.get(position);

        holder.setDateAndFood(date, food);
        holder.setStreak(date, food);
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}
