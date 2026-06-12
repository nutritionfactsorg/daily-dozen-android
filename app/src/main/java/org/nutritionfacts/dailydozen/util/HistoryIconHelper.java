package org.nutritionfacts.dailydozen.util;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Locale;

/**
 * Iconify reads color from the {@code {fa-icon ... @color/...}} token in the text string, which
 * overrides {@code android:textColor} and may not resolve all color resources. Set icons in code
 * with an explicit hex color instead.
 */
public final class HistoryIconHelper {
    private HistoryIconHelper() {
    }

    public static void applyCalendarIcon(@NonNull final IconTextView view) {
        applyThemedIcon(view, "fa-calendar", 16);
    }

    public static void applyChartIcon(@NonNull final IconTextView view) {
        applyThemedIcon(view, "fa-bar-chart", 20);
    }

    public static void applyThemedIcon(
            @NonNull final IconTextView view,
            @NonNull final String iconKey,
            final int sizeDp) {
        final int color = MaterialColors.getColor(
                view.getContext(),
                com.google.android.material.R.attr.colorOnSurface,
                0xFF000000);
        view.setText(String.format(
                Locale.US,
                "{%s %ddp %s}",
                iconKey,
                sizeDp,
                toIconifyHex(color)));
    }

    @NonNull
    private static String toIconifyHex(@ColorInt final int color) {
        return String.format(Locale.US, "#%06X", (0xFFFFFF & color));
    }
}
