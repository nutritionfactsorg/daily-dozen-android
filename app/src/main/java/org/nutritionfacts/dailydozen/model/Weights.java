package org.nutritionfacts.dailydozen.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "weights")
public class Weights extends TruncatableModel {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "morning_weight")
    private float morningWeight;

    @Column(name = "evening_weight")
    private float eveningWeight;

    public Weights() {
    }

    public Weights(Day day, float morningWeight, float eveningWeight) {
        this.day = day;
        this.morningWeight = morningWeight;
        this.eveningWeight = eveningWeight;
    }

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

    public Float getAverageWeight() {
        if (morningWeight != 0 && eveningWeight != 0) {
            return (morningWeight + eveningWeight) / 2;
        } else if (morningWeight != 0) {
            return morningWeight;
        } else if (eveningWeight != 0) {
            return eveningWeight;
        }
        return null;
    }

    public static Weights createWeightsIfDoesNotExist(final Day day, float morningWeight, float eveningWeight) {
        Weights weights = getWeightsOnDay(day);

        if (weights == null) {
            weights = new Weights(day, morningWeight, eveningWeight);
        } else {
            weights.setMorningWeight(morningWeight);
            weights.setEveningWeight(eveningWeight);
        }

        weights.save();

        return weights;
    }

    public static Weights getWeightsOnDay(final Day day) {
        if (day != null && day.getId() != null) {
            return new Select().from(Weights.class)
                    .where("date_id = ?", day.getId())
                    .executeSingle();
        }

        return null;
    }

    public static boolean isEmpty() {
        return new Select().from(Weights.class).count() == 0;
    }
}
