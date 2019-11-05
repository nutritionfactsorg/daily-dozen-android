package org.nutritionfacts.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "tweaks")
public class Tweak extends Model {
    @Column(name = "name", index = true)
    private String name;

    @Column(name = "id_name", index = true)
    private String idName;

    @Column(name = "recommended_amount")
    private int recommendedAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public int getRecommendedAmount() {
        return recommendedAmount;
    }

    public Tweak() {
    }

    public Tweak(String name, String idName, int recommendedAmount) {
        this.name = name;
        this.idName = idName;
        this.recommendedAmount = recommendedAmount;
    }

    @Override
    public String toString() {
        return name;
    }

    // TODO (slavick) use the Food class as a guide
}
