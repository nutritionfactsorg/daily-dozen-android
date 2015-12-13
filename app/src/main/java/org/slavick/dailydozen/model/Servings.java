package org.slavick.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "servings")
public class Servings extends Model {
    @Column(name = "date_id")
    private Date date;

    @Column(name = "food_id")
    private Food food;

    @Column(name = "servings")
    private int servings;

    @Override
    public String toString() {
        return String.format("[%s] [%s] [Servings %s]", food.toString(), date.toString(), servings);
    }
}
