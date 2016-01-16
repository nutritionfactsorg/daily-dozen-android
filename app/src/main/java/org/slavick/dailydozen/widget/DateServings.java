package org.slavick.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;

import org.slavick.dailydozen.R;

public class DateServings extends CardViewHeader {
    public DateServings(Context context) {
        super(context);
        init(context);
    }

    public DateServings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        setHeader(context.getString(R.string.servings));
    }

    public void setServings(final int servingsOnDate) {
        setSubHeader(String.format("%s out of 24", servingsOnDate));
    }
}
