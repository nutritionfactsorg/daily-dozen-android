package org.nutritionfacts.dailydozen.util;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.nutritionfacts.dailydozen.controller.Bus;

import java.util.List;

public final class WideScreenLayout {
    private static final int TWO_COLUMN_MIN_WIDTH_DP = 600;

    private WideScreenLayout() {
    }

    public static boolean useTwoColumns(Resources resources) {
        return resources.getConfiguration().screenWidthDp >= TWO_COLUMN_MIN_WIDTH_DP;
    }

    /**
     * Splits items evenly across two columns in list order. When the count is odd, the left
     * column receives the extra item.
     */
    public static void distributeViews(
            LinearLayout leftColumn,
            LinearLayout rightColumn,
            Resources resources,
            List<View> items) {
        if (items.isEmpty()) {
            return;
        }

        if (!useTwoColumns(resources)) {
            for (View item : items) {
                leftColumn.addView(item);
            }
            return;
        }

        final int splitIndex = leftColumnItemCount(items.size());
        for (int i = 0; i < items.size(); i++) {
            if (i < splitIndex) {
                leftColumn.addView(items.get(i));
            } else {
                rightColumn.addView(items.get(i));
            }
        }
    }

    private static int leftColumnItemCount(int totalItems) {
        return (totalItems + 1) / 2;
    }

    public static void unregisterBusInTree(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            final View child = root.getChildAt(i);
            Bus.unregister(child);
            if (child instanceof ViewGroup) {
                unregisterBusInTree((ViewGroup) child);
            }
        }
    }
}
