package org.nutritionfacts.dailydozen.util;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.android.material.color.MaterialColors;

public final class HistoryChartHelper {
    private HistoryChartHelper() {
    }

    public static void applyTheme(@NonNull final CombinedChart chart, @NonNull final Context context) {
        final int textColor = themeColor(context, com.google.android.material.R.attr.colorOnSurface);
        final int gridColor = themeColor(context, com.google.android.material.R.attr.colorOutlineVariant);

        chart.setDescriptionColor(textColor);
        chart.getLegend().setTextColor(textColor);

        applyAxisTheme(chart.getXAxis(), textColor, gridColor);
        applyAxisTheme(chart.getAxisLeft(), textColor, gridColor);
        applyAxisTheme(chart.getAxisRight(), textColor, gridColor);
    }

    private static void applyAxisTheme(
            @NonNull final XAxis axis,
            @ColorInt final int textColor,
            @ColorInt final int gridColor) {
        axis.setTextColor(textColor);
        axis.setAxisLineColor(gridColor);
        axis.setGridColor(gridColor);
    }

    private static void applyAxisTheme(
            @NonNull final YAxis axis,
            @ColorInt final int textColor,
            @ColorInt final int gridColor) {
        axis.setTextColor(textColor);
        axis.setAxisLineColor(gridColor);
        axis.setGridColor(gridColor);
    }

    @ColorInt
    private static int themeColor(@NonNull final Context context, final int attr) {
        return MaterialColors.getColor(context, attr, 0);
    }
}
