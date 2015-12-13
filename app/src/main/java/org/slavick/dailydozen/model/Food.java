package org.slavick.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "foods")
public class Food extends Model {
    @Column(name = "name", index = true)
    private String name;

    @Column(name = "recommended_servings")
    private int recommendedServings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecommendedServings() {
        return recommendedServings;
    }

    public void setRecommendedServings(int recommendedServings) {
        this.recommendedServings = recommendedServings;
    }

    public Food() {
    }

    public Food(String name, int recommendedServings) {
        this.name = name;
        this.recommendedServings = recommendedServings;
    }

    @Override
    public String toString() {
        return String.format("Food %s (recommended servings %s)", name, recommendedServings);
    }
}
