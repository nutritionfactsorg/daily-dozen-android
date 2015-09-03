package com.qxmd.qxrecyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chankruse on 15-01-05.
 */
public class QxRecyclerView extends RecyclerView {
    public QxRecyclerView(Context context) {
        super(context);
    }

    public QxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QxRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
