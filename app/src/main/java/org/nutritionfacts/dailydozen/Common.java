package org.nutritionfacts.dailydozen;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.google.android.material.color.MaterialColors;

import org.nutritionfacts.dailydozen.activity.InfoActivity;
import org.nutritionfacts.dailydozen.activity.ServingsHistoryActivity;
import org.nutritionfacts.dailydozen.activity.WeightHistoryActivity;
import org.nutritionfacts.dailydozen.activity.DateSelectionHost;
import org.nutritionfacts.dailydozen.model.DDServings;
import org.nutritionfacts.dailydozen.model.Day;
import org.nutritionfacts.dailydozen.model.Food;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;
import org.nutritionfacts.dailydozen.model.TweakServings;
import org.nutritionfacts.dailydozen.model.Weights;
import org.nutritionfacts.dailydozen.model.enums.HistoryType;
import org.nutritionfacts.dailydozen.util.DateUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Common {
    public static final String FILE_PROVIDER_AUTHORITY = "org.nutritionfacts.dailydozen.fileprovider";
    public static final String PREFERENCES_FILE = "org.nutritionfacts.dailydozen.preferences";
    public static final String BACKUP_FILE_PREFIX = "dailydozen_backup";
    public static final String BACKUP_FILE_SUFFIX = ".json";
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static final int MAX_SERVINGS = 24;
    public static final int MAX_TWEAKS_SERVINGS = 37;

    public static final String EXERCISE = "exercise";
    public static final String VITAMIN_B12 = "Vitamin B12";

    public static final String MEAL = "meal";
    public static final String DAILY_DOSE = "dailydose";
    public static final String DAILY = "daily";
    public static final String NIGHTLY = "nightly";

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
                    .setPositiveButton(R.string.rate_now, (dialog, which) -> {
                        Common.openPlayStore(context);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.not_now, (dialog, which) -> dialog.dismiss())
                    .setOnDismissListener(dialog -> userIsBeingAsked = false)
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
                imageView.setImageDrawable(AppCompatResources.getDrawable(context, imageId));
                return true;
            } catch (OutOfMemoryError e) {
                imageView.setVisibility(View.GONE);
            }
        }

        return false;
    }

    @ColorInt
    public static int getListItemColorForPosition(final Context context, final int position) {
        final int colorAttr = position % 2 == 0
                ? com.google.android.material.R.attr.colorSurface
                : com.google.android.material.R.attr.colorSurfaceContainerLow;
        return MaterialColors.getColor(context, colorAttr, 0);
    }

    private static Intent createItemIntent(
            final Context context,
            final Class<? extends AppCompatActivity> klass,
            final String idExtraKey,
            final long id) {
        final Intent intent = new Intent(context, klass);
        intent.putExtra(idExtraKey, id);
        return intent;
    }

    public static Intent createShowDateIntent(final int year, final int month, final int day) {
        final Intent showDateIntent = new Intent();
        showDateIntent.putExtra(Args.DATE, DateUtil.getCalendarForYearMonthAndDay(year, month, day).getTime());
        return showDateIntent;
    }

    public static void openFood(final Context context, final Food food, final boolean scrollToHistory) {
        if (isSupplement(food) && !scrollToHistory) {
            openUrlInExternalBrowser(context, FoodInfo.getFoodTypeVideosLink(food.getName()));
            return;
        }

        final Intent intent = createItemIntent(context, InfoActivity.class, Args.FOOD_ID, food.getId());
        if (scrollToHistory) {
            intent.putExtra(Args.SCROLL_TO_HISTORY, true);
        }
        startSelectableDateActivity(context, intent);
    }

    public static void openTweak(final Context context, final Tweak tweak, final boolean scrollToHistory) {
        final Intent intent = createItemIntent(context, InfoActivity.class, Args.TWEAK_ID, tweak.getId());
        if (scrollToHistory) {
            intent.putExtra(Args.SCROLL_TO_HISTORY, true);
        }
        startSelectableDateActivity(context, intent);
    }

    public static boolean isSupplement(final Food food) {
        return food != null && VITAMIN_B12.equalsIgnoreCase(food.getIdName());
    }

    public static void openChartHistory(final Context context, @HistoryType.Interface final int historyType) {
        final Intent intent = new Intent(context, ServingsHistoryActivity.class);
        intent.putExtra(Args.HISTORY_TYPE, historyType);
        startSelectableDateActivity(context, intent);
    }

    public static void openWeightHistory(final Context context) {
        startSelectableDateActivity(context, new Intent(context, WeightHistoryActivity.class));
    }

    private static void startSelectableDateActivity(final Context context, final Intent intent) {
        if (context instanceof DateSelectionHost) {
            ((DateSelectionHost) context).launchForDateSelection(intent);
        } else if (context instanceof Activity) {
            context.startActivity(intent);
        }
    }

    public static void truncateAllDatabaseTables() {
        DDServings.truncate(DDServings.class);
        TweakServings.truncate(TweakServings.class);
        Weights.truncate(Weights.class);
        Day.truncate(Day.class);
    }

    public static File createBackupFile(final File filesDir) {
        final String timestamp = BACKUP_TIMESTAMP_FORMAT.format(LocalDateTime.now());
        return new File(filesDir, BACKUP_FILE_PREFIX + "_" + timestamp + BACKUP_FILE_SUFFIX);
    }

    public static boolean isDailyDozenBackupFileName(final String name) {
        if (name == null) {
            return false;
        }
        final String lower = name.toLowerCase(Locale.ROOT);
        return lower.startsWith(BACKUP_FILE_PREFIX) && lower.endsWith(BACKUP_FILE_SUFFIX);
    }
}
