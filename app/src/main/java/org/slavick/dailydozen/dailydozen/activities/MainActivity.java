package org.slavick.dailydozen.dailydozen.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import org.slavick.dailydozen.dailydozen.R;
import org.slavick.dailydozen.dailydozen.widgets.FoodItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.main_form)
    protected ViewGroup form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        createFoodItems();
    }

    private void createFoodItems() {
        final String[] names = getResources().getStringArray(R.array.food_names);
        final int[] quantities = getResources().getIntArray(R.array.food_quantities);

        for (int i = 0; i < names.length; i++) {
            form.addView(new FoodItem(this, names[i], quantities[i]));
        }
    }
}
