package org.nutritionfacts.dailydozen.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

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

    public static float getAverageWeightInMonth(final int year, final int monthOneBased) {
        return getAverageWeightForDays(Day.getDaysInYearAndMonth(year, monthOneBased));
    }

    public static float getAverageWeightInYear(final int year) {
        return getAverageWeightForDays(Day.getDaysInYear(year));
    }

    private static float getAverageWeightForDays(final List<Day> days) {
        if (days == null || days.isEmpty()) {
            return 0;
        }

        int totalWeight = 0;
        int daysWithWeights = 0;

        for (Day day : days) {
            Weights weights = getWeightsOnDay(day);
            if (weights != null) {
                Float averageWeight = weights.getAverageWeight();
                if (averageWeight != null) {
                    totalWeight += averageWeight;
                    daysWithWeights++;
                }
            }
        }

        if (daysWithWeights == 0) {
            return 0;
        } else {
            return (float) totalWeight / daysWithWeights;
        }
    }
}
