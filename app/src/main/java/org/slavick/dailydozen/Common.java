package org.slavick.dailydozen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Common {
    public static final String FILE_PROVIDER_AUTHORITY = "org.slavick.dailydozen.fileprovider";
    public static final String PREFERENCES_FILE = "org.slavick.dailydozen.preferences";

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
        showToast(context, context.getString(R.string.not_implemented_yet));
    }

    public static void showToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(final Context context, final int stringId) {
        showToast(context, context.getString(stringId));
    }

    // Calculates the number of days between epoch == 0 (Jan 1, 1970) and now
    public static int getDaysSinceEpoch() {
        return getEpoch().numDaysFrom(DateTime.forInstant(new Date().getTime(), TimeZone.getDefault())) + 1;
    }

    @NonNull
    public static DateTime getEpoch() {
        return DateTime.forInstant(0, TimeZone.getDefault());
    }
}
