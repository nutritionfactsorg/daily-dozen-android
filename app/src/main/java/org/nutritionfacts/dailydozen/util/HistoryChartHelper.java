package org.nutritionfacts.dailydozen.util;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.color.MaterialColors;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.event.LoadHistoryCompleteEvent;
import org.nutritionfacts.dailydozen.model.enums.TimeScale;

import java.text.DecimalFormat;
import java.util.List;

public final class HistoryChartHelper {
    public static final String BAR_VALUE_FORMAT_INTEGER = "#";
    public static final String BAR_VALUE_FORMAT_ONE_DECIMAL = "#.0";

    private HistoryChartHelper() {
    }

    public static LoadHistoryCompleteEvent completeEvent(
            final CombinedData combinedData,
            @TimeScale.Interface final int timeScale) {
        return new LoadHistoryCompleteEvent(combinedData, timeScale);
    }

    public static CombinedData lineAndBarData(
            @NonNull final Context context,
            @NonNull final List<String> xLabels,
            @NonNull final List<Entry> lineEntries,
            @NonNull final List<BarEntry> barEntries,
            @NonNull final String barLabel,
            @NonNull final String barValueFormatPattern) {
        final CombinedData combinedData = lineData(context, xLabels, lineEntries);
        combinedData.setData(barData(context, xLabels, barEntries, barLabel, barValueFormatPattern));
        return combinedData;
    }

    public static CombinedData lineData(
            @NonNull final Context context,
            @NonNull final List<String> xLabels,
            @NonNull final List<Entry> lineEntries) {
        final CombinedData combinedData = new CombinedData(xLabels);
        combinedData.setData(lineDataSet(context, xLabels, lineEntries));
        return combinedData;
    }

    // Calculates an exponentially smoothed moving average with 10% smoothing
    public static float calculateTrend(final float previousTrend, final float currentValue) {
        if (previousTrend == 0) {
            return currentValue;
        }
        // Tn = Tn-1 + 0.1 * (Vn - Tn-1)
        return previousTrend + 0.1f * (currentValue - previousTrend);
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

    public static void presentChartData(
            @NonNull final CombinedChart chart,
            @NonNull final Context context,
            @NonNull final CombinedData chartData,
            @TimeScale.Interface final int timeScale,
            @NonNull final YAxisConfigurer yAxisConfigurer,
            @Nullable final OnChartValueSelectedListener valueSelectedListener) {
        chart.setData(chartData);
        applyTheme(chart, context);

        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        chart.setVisibleXRange(5, 10);
        chart.getXAxis().setDrawLabels(true);

        // Without this line, MPAndroidChart v2.1.6 cuts off the tops of the X-axis date labels
        chart.setExtraTopOffset(4f);

        chart.moveViewToX(chart.getXChartMax());
        chart.setDescription("");
        chart.setDrawValueAboveBar(false);

        chart.getAxisRight().setEnabled(false);
        yAxisConfigurer.configure(chart.getAxisLeft());

        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        chart.setOnChartValueSelectedListener(valueSelectedListener);
        chart.setHighlightPerTapEnabled(timeScale == TimeScale.DAYS);
    }

    public static YAxisConfigurer fixedServingsAxis(final int maxServings) {
        return axis -> {
            axis.setAxisMinValue(0);
            axis.setAxisMaxValue(maxServings);
            axis.setEnabled(false);
        };
    }

    public static YAxisConfigurer dynamicWeightAxis(final float minVal, final float maxVal) {
        return axis -> {
            axis.setAxisMinValue(minVal - 5);
            axis.setAxisMaxValue(maxVal + 5);
        };
    }

    public interface YAxisConfigurer {
        void configure(@NonNull YAxis axis);
    }

    private static BarData barData(
            @NonNull final Context context,
            @NonNull final List<String> xLabels,
            @NonNull final List<BarEntry> barEntries,
            @NonNull final String barLabel,
            @NonNull final String barValueFormatPattern) {
        final BarDataSet dataSet = new BarDataSet(barEntries, barLabel);

        dataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(context, android.R.color.white));
        dataSet.setValueTextSize(14);
        dataSet.setValueFormatter(new BarChartValueFormatter(barValueFormatPattern));

        return new BarData(xLabels, dataSet);
    }

    private static LineData lineDataSet(
            @NonNull final Context context,
            @NonNull final List<String> xLabels,
            @NonNull final List<Entry> lineEntries) {
        final LineDataSet dataSet = new LineDataSet(lineEntries, context.getString(R.string.moving_average));

        final int color = ContextCompat.getColor(context, R.color.brown);

        dataSet.setColor(color);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(color);
        dataSet.setFillColor(color);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12);
        dataSet.setValueTextColor(color);
        dataSet.setValueFormatter(new LineChartValueFormatter());

        return new LineData(xLabels, dataSet);
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

    private static final class BarChartValueFormatter implements ValueFormatter {
        private final DecimalFormat decimalFormat;

        BarChartValueFormatter(@NonNull final String pattern) {
            decimalFormat = new DecimalFormat(pattern);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return decimalFormat.format(value);
        }
    }

    private static final class LineChartValueFormatter implements ValueFormatter {
        private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return decimalFormat.format(value);
        }
    }
}
