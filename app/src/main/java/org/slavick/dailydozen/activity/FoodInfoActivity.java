package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.model.FoodInfo;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FoodInfoActivity extends AppCompatActivity {
    @Bind(R.id.food_types)
    protected ListView lvFoodTypes;

    @Bind(R.id.food_serving_sizes)
    protected ListView lvFoodServingSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_info);
        ButterKnife.bind(this);

        displayInfoForFood();
    }

    private void displayInfoForFood() {
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Args.FOOD_NAME)) {
            final String foodName = intent.getStringExtra(Args.FOOD_NAME);

            initList(lvFoodTypes, FoodInfo.getTypesOfFood(foodName));
            initList(lvFoodServingSizes, FoodInfo.getServingSizes(foodName));
        }
    }

    private void initList(final ListView listView, final List<String> items) {
        listView.setAdapter(createAdapter(items));
        fullyExpandList(listView);
    }

    private ArrayAdapter<String> createAdapter(final List<String> items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    public void fullyExpandList(final ListView list) {
        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getListViewHeight(list)));
    }

    private int getListViewHeight(final ListView list) {
        final Adapter adapter = list.getAdapter();
        final int count = adapter.getCount();

        list.measure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // The (count - 1) hides the final list item divider
        return list.getMeasuredHeight() * count + ((count - 1) * list.getDividerHeight());
    }
}
