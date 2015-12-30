package org.slavick.dailydozen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slavick.dailydozen.Args;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.adapter.FoodServingsAdapter;
import org.slavick.dailydozen.model.Servings;
import org.slavick.dailydozen.widget.FoodServings;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateFragment extends Fragment implements FoodServings.ClickListener {
    private final static String TAG = DateFragment.class.getSimpleName();

    private Date date;

    @Bind(R.id.score)
    protected TextView tvScore;

    @Bind(R.id.date_food_servings)
    protected RecyclerView lvFoodServings;

    public static DateFragment newInstance(final Date date) {
        final Bundle args = new Bundle();
        args.putSerializable(Args.DATE, date);

        final DateFragment dateFragment = new DateFragment();
        dateFragment.setArguments(args);
        return dateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_date, container, false);
        ButterKnife.bind(this, view);

        displayFormForDate();

        return view;
    }

    private void displayFormForDate() {
        final Bundle args = getArguments();

        if (args != null && args.containsKey(Args.DATE)) {
            date = (Date) args.getSerializable(Args.DATE);

            if (date != null) {
                lvFoodServings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                lvFoodServings.setAdapter(new FoodServingsAdapter(this, date));

                updateScore();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onServingsChanged() {
        updateScore();
    }

    private void updateScore() {
        tvScore.setText(String.format("%s/24", Servings.getTotalServingsOnDate(date)));
    }
}
