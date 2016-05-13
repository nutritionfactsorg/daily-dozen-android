package org.nutritionfacts.dailydozen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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

    private static boolean userIsBeingAsked;

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

    public static void askUserToRateApp(final Context context) {
        if (!userIsBeingAsked) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.dialog_ask_user_to_rate_app_title)
                    .setMessage(R.string.dialog_ask_user_to_rate_app_message)
                    .setPositiveButton(R.string.rate_now, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Common.openPlayStore(context);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            userIsBeingAsked = false;
                        }
                    })
                    .create().show();

            userIsBeingAsked = true;
        }
    }

    public static void openPlayStore(final Context context) {
        try {
            context.startActivity(createOpenPlayStoreIntent(context, "market://details?id="));
        } catch (ActivityNotFoundException e) {
            context.startActivity(createOpenPlayStoreIntent(context, "https://play.google.com/store/apps/details?id="));
        }
    }

    private static Intent createOpenPlayStoreIntent(final Context context, final String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url + context.getPackageName()));
    }
}
