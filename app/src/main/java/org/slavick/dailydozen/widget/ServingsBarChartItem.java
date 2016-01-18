package org.slavick.dailydozen.widget;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.slavick.dailydozen.R;

public class ServingsBarChartItem extends RecyclerView.ViewHolder {
    private View emptyBar;
    private View servingsBar;
    private TextView servingsValue;

    public ServingsBarChartItem(View itemView) {
        super(itemView);

        emptyBar = itemView.findViewById(R.id.empty_bar);
        servingsBar = itemView.findViewById(R.id.servings_bar);
        servingsValue = (TextView) itemView.findViewById(R.id.servings_value);
    }

    public void setServings(int numServings) {
        if (numServings >= 24) {
            numServings = 24;
        }

        setWeight(emptyBar, 24 - numServings);
        setWeight(servingsBar, numServings);

        servingsValue.setText(String.valueOf(numServings));
    }

    private void setWeight(View view, int amount) {
        Log.d("SLAVICK", "setServings: " + amount);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.weight = amount;
        view.setLayoutParams(lp);
    }
}
