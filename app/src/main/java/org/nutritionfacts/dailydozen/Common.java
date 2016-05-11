package org.nutritionfacts.dailydozen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Common {
    public static final String FILE_PROVIDER_AUTHORITY = "org.nutritionfacts.dailydozen.fileprovider";
    public static final String PREFERENCES_FILE = "org.nutritionfacts.dailydozen.preferences";

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
        showToast(context, R.string.not_implemented_yet);
    }

    public static void showToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(final Context context, final int stringId) {
        showToast(context, context.getString(stringId));
    }

    public static int convertDpToPx(final Context context, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static void openUrlInExternalBrowser(final Context context, final int urlId) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(urlId))));
    }
}
