package org.nutritionfacts.dailydozen.model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "tweak_servings")
public class TweakServings extends TruncatableModel {
    @Column(name = "date_id")
    private Day day;

    @Column(name = "tweak_id")
    private Tweak tweak;

    @Column(name = "servings")
    private int servings;

    @Column(name = "streak")
    private int streak;

    public TweakServings() {
    }

    public TweakServings(Day day, Tweak tweak) {
        this.day = day;
        this.tweak = tweak;
    }

    public Day getDay() {
        return day;
    }

    public Tweak getTweak() {
        return tweak;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}
