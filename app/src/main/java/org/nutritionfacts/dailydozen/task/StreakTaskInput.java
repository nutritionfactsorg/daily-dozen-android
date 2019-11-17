package org.nutritionfacts.dailydozen.task;

import org.nutritionfacts.dailydozen.RDA;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;

public class StreakTaskInput {
    private final Day startingDay;
    private final RDA rda;

    public StreakTaskInput(final Day startingDay, final RDA rda) {
        this.startingDay = startingDay;
        this.rda = rda;
    }

    Day getStartingDay() {
        return startingDay;
    }

    public Food getFood() {
        return (Food) rda;
    }

    public Tweak getTweak() {
        return (Tweak) rda;
    }
}
