package org.slavick.dailydozen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Common {
    public static void fullyExpandList(final ListView list) {
        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getListViewHeight(list)));
    }

    private static int getListViewHeight(final ListView list) {
        final Adapter adapter = list.getAdapter();
        final int count = adapter.getCount();

        list.measure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // The (count - 1) hides the final list item divider
        return list.getMeasuredHeight() * count + ((count - 1) * list.getDividerHeight());
    }

    public static void showNotImplementedYet(final Context context) {
        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show();
    }
}
