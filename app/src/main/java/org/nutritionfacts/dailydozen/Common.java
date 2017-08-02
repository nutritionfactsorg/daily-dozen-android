package org.nutritionfacts.dailydozen;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.nutritionfacts.dailydozen.activity.FoodHistoryActivity;
import org.nutritionfacts.dailydozen.activity.FoodInfoActivity;
import org.nutritionfacts.dailydozen.activity.ServingsHistoryActivity;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;

import java.util.Date;

public class Common {
    public static final String FILE_PROVIDER_AUTHORITY = "org.nutritionfacts.dailydozen.fileprovider";
    public static final String PREFERENCES_FILE = "org.nutritionfacts.dailydozen.preferences";

    public static final int MAX_SERVINGS = 24;

    public static final String EXERCISE = "exercise";
    public static final String VITAMIN_B12 = "Vitamin B12";
    public static final String VITAMIN_D = "Vitamin D";

    private static boolean userIsBeingAsked;

    private Common() {
        // hide constructor
    }

    public static void showToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(final Context context, final int stringId) {
        showToast(context, context.getString(stringId));
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    public static void openUrlInExternalBrowser(final Context context, final int urlId) {
        openUrlInExternalBrowser(context, context.getString(urlId));
    }

    public static void openUrlInExternalBrowser(final Context context, final String url) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            showToast(context, R.string.error_cannot_handle_url);
        }
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

    private static void openPlayStore(final Context context) {
        try {
            context.startActivity(createOpenPlayStoreIntent(context, "market://details?id="));
        } catch (ActivityNotFoundException e) {
            context.startActivity(createOpenPlayStoreIntent(context, "https://play.google.com/store/apps/details?id="));
        }
    }

    private static Intent createOpenPlayStoreIntent(final Context context, final String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url + context.getPackageName()));
    }

    // This method is for loading images in a way that protects against crashes due to OutOfMemoryErrors
    public static boolean loadImage(final Context context, final ImageView imageView, final Integer imageId) {
        if (imageId != null) {
            try {
                imageView.setImageDrawable(ContextCompat.getDrawable(context, imageId));
                return true;
            } catch (OutOfMemoryError e) {
                imageView.setVisibility(View.GONE);
            }
        }

        return false;
    }

    @ColorInt
    public static int getListItemColorForPosition(final Context context, final int position) {
        return ContextCompat.getColor(context, position % 2 == 0 ? android.R.color.white : R.color.gray_light);
    }

    private static Intent createFoodIntent(final Context context, final Class<? extends AppCompatActivity> klass, final Food food) {
        final Intent intent = new Intent(context, klass);
        intent.putExtra(Args.FOOD_ID, food.getId());
        return intent;
    }

    public static Intent createShowDateIntent(final Date date) {
        final Intent showDateIntent = new Intent();
        showDateIntent.putExtra(Args.DATE, date);
        return showDateIntent;
    }

    public static void openFoodInfo(final Context context, final Food food) {
        FoodInfo.initFoodInfo(context);

        if (isVitamin(food)) {
            openUrlInExternalBrowser(context, FoodInfo.getFoodTypeVideosLink(food.getName()));
        } else {
            context.startActivity(createFoodIntent(context, FoodInfoActivity.class, food));
        }
    }

    public static boolean isVitamin(final Food food) {
        return food != null &&
                (VITAMIN_B12.equalsIgnoreCase(food.getIdName()) || VITAMIN_D.equalsIgnoreCase(food.getIdName()));
    }

    public static void openFoodHistory(final Context context, final Food food) {
        startSelectableDateActivity(context, createFoodIntent(context, FoodHistoryActivity.class, food));
    }

    public static void openServingsHistory(final Context context) {
        startSelectableDateActivity(context, new Intent(context, ServingsHistoryActivity.class));
    }

    private static void startSelectableDateActivity(final Context context, final Intent intent) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, Args.SELECTABLE_DATE_REQUEST);
        }
    }
}
