package org.slavick.dailydozen.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.slavick.dailydozen.model.Food;
import org.slavick.dailydozen.widget.FoodServings;

import java.util.Date;
import java.util.List;

public class FoodServingsAdapter extends BaseAdapter {
    final private Date date;
    final private List<Food> foods;

    public FoodServingsAdapter(Date date) {
        this.date = date;
        this.foods = Food.getAllFoods();
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodServings foodServings;
        if (convertView == null) {
            foodServings = new FoodServings(parent.getContext());
        } else {
            foodServings = (FoodServings) convertView;
        }

        foodServings.setDateAndFood(date, (Food) getItem(position));

        return foodServings;
    }
}
