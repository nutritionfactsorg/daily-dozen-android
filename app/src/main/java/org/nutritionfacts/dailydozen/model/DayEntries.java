package org.nutritionfacts.dailydozen.model;

import java.util.Map;

public class DayEntries {
    private String Date;
    private float morningWeight;
    private float eveningWeight;
    private Map<String, Integer> dailyDozen;
    private Map<String, Integer> tweaks;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setWeights(Weights weights) {
        if (weights != null) {
            morningWeight = weights.getMorningWeight();
            eveningWeight = weights.getEveningWeight();
        }
    }

    public float getMorningWeight() {
        return morningWeight;
    }

    public float getEveningWeight() {
        return eveningWeight;
    }

    public Map<String, Integer> getDailyDozen() {
        return dailyDozen;
    }

    public void setDailyDozen(Map<String, Integer> dailyDozen) {
        this.dailyDozen = dailyDozen;
    }

    public Map<String, Integer> getTweaks() {
        return tweaks;
    }

    public void setTweaks(Map<String, Integer> tweaks) {
        this.tweaks = tweaks;
    }
}
