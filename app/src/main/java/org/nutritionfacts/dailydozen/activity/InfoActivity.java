package org.nutritionfacts.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.Tweak;

public abstract class InfoActivity extends DailyDozenActivity {
    private Food food;
    private Tweak tweak;

    public Food getFood() {
        return food;
    }

    public Tweak getTweak() {
        return tweak;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadFoodOrTweakFromIntent();

        if (food == null && tweak == null) {
            finish();
        }
    }

    private void loadFoodOrTweakFromIntent() {
        final Intent intent = getIntent();
        if (intent != null) {
            food = Food.getById(intent.getLongExtra(Args.FOOD_ID, -1));
            if (food != null) {
                setTitle(food.getName());
                return;
            }

            tweak = Tweak.getById(intent.getLongExtra(Args.TWEAK_ID, -1));
            if (tweak != null) {
                setTitle(tweak.getName());
            }
        }
    }
}
