package org.nutritionfacts.dailydozen.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "weights")
public class Weights extends Model {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "morning_weight")
    private float morningWeight;

    @Column(name = "evening_weight")
    private float eveningWeight;

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public float getMorningWeight() {
        return morningWeight;
    }

    public void setMorningWeight(float morningWeight) {
        this.morningWeight = morningWeight;
    }

    public float getEveningWeight() {
        return eveningWeight;
    }

    public void setEveningWeight(float eveningWeight) {
        this.eveningWeight = eveningWeight;
    }
    
    public static Weights getWeightsOnDay(final Day day) {
        Weights weights = new Weights();

        if (day != null && day.getId() != null) {
            weights = new Select().from(Weights.class)
                    .where("date_id = ?", day.getId())
                    .executeSingle();
        }

        return weights;
    }
}
