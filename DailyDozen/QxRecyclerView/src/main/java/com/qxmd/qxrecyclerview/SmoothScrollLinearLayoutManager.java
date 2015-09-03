package com.qxmd.qxrecyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by chankruse on 15-08-18.
 */
public class SmoothScrollLinearLayoutManager extends LinearLayoutManager {

    private Context context;

    private boolean hasSetSnapPreference = false;
    private int snapPreference;

    public SmoothScrollLinearLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    public SmoothScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    public SmoothScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void setSnapPreference(int snapPreference) {
        hasSetSnapPreference = true;
        this.snapPreference = snapPreference;
    }

    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, RecyclerView.State state, int position)
    {
        // A good idea would be to create this instance in some initialization method, and just set the target position in this method.
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(SmoothScrollLinearLayoutManager.this.context)
        {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return ((LinearLayoutManager)recyclerView.getLayoutManager()).computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 35f / displayMetrics.densityDpi;
            }

            @Override
            protected int getVerticalSnapPreference() {
                if (hasSetSnapPreference) {
                    return snapPreference;
                } else {
                    return super.getVerticalSnapPreference();
                }
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
