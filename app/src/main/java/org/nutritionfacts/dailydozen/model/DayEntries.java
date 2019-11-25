package org.nutritionfacts.dailydozen.model;

import java.util.Map;

public class DayEntries {
    private String Date;
    private Float morningWeight;
    private Float eveningWeight;
    private Map<String, Integer> dailyDozen;
    private Map<String, Integer> tweaks;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setWeights(Weights weights) {
        morningWeight = weights.getMorningWeight();
        eveningWeight = weights.getEveningWeight();
    }

    public Float getMorningWeight() {
        return morningWeight;
    }

    public Float getEveningWeight() {
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
