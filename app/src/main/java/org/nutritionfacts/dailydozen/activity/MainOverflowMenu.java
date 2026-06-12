package org.nutritionfacts.dailydozen.activity;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;
import org.nutritionfacts.dailydozen.widget.ThemeToggleView;

import java.util.ArrayList;
import java.util.List;

final class MainOverflowMenu {
    private static final int[] OVERFLOW_ITEM_IDS = {
            R.id.menu_toggle_modes,
            R.id.menu_latest_videos,
            R.id.menu_how_not_to_die,
            R.id.menu_cookbook,
            R.id.menu_how_not_to_diet,
            R.id.menu_daily_dozen_challenge,
            R.id.menu_faq,
            R.id.menu_donate,
            R.id.menu_subscribe,
            R.id.menu_open_source,
            R.id.menu_daily_reminder_settings,
            R.id.menu_backup,
            R.id.menu_restore,
            R.id.menu_about,
            R.id.menu_debug,
    };

    interface Callback {
        boolean onMenuItemClick(int itemId);
    }

    @Nullable
    private PopupWindow popupWindow;

    void hookOverflowButton(@NonNull final MaterialToolbar toolbar, @NonNull final Callback callback) {
        toolbar.post(() -> {
            final View overflowButton = findOverflowMenuButton(toolbar);
            if (overflowButton == null) {
                return;
            }
            overflowButton.setOnClickListener(v -> show(toolbar, v, callback));
        });
    }

    void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void show(
            @NonNull final MaterialToolbar toolbar,
            @NonNull final View anchor,
            @NonNull final Callback callback) {
        dismiss();

        final Context popupContext = new ContextThemeWrapper(
                resolveActivityContext(toolbar.getContext()),
                R.style.ThemeOverlay_AppTheme_ToolbarPopup);
        final LayoutInflater inflater = LayoutInflater.from(popupContext);
        final View content = inflater.inflate(R.layout.popup_main_overflow, null);

        bindThemeToggle(content, popupContext);

        final List<MenuItem> overflowItems = collectOverflowItems(toolbar);
        final int popupWidth = anchor.getWidth() > 0
                ? Math.max(anchor.getWidth(), dp(popupContext, 220))
                : dp(popupContext, 220);

        populateMenuRows(
                content.findViewById(R.id.overflow_menu_items),
                inflater,
                overflowItems,
                callback);
        applyScrollMaxHeight(content, anchor, popupContext, popupWidth);

        popupWindow = new PopupWindow(
                content,
                popupWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setElevation(dp(popupContext, 8));
        popupWindow.showAsDropDown(anchor, 0, 0, Gravity.END);
    }

    private void bindThemeToggle(@NonNull final View content, @NonNull final Context context) {
        final ThemeToggleView toggle = content.findViewById(R.id.theme_toggle);
        final boolean dark = Prefs.getInstance(context).isDarkMode();
        toggle.setDarkMode(dark, false);
        toggle.setOnThemeToggleListener(() -> {
            dismiss();
            Prefs.getInstance(context).toggleDarkMode(context);
            if (context instanceof Activity activity) {
                activity.recreate();
            }
        });
    }

    private void applyScrollMaxHeight(
            @NonNull final View content,
            @NonNull final View anchor,
            @NonNull final Context context,
            final int popupWidth) {
        final ScrollView scrollView = content.findViewById(R.id.overflow_menu_scroll);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.EXACTLY);
        final int unspecifiedHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        scrollView.measure(widthSpec, unspecifiedHeight);
        final int desiredScrollHeight = scrollView.getMeasuredHeight();

        final int headerHeight = measureHeaderHeight(content, popupWidth);
        final int maxAvailableHeight = getMaxAvailablePopupHeight(anchor);
        final int maxScrollHeight = Math.max(0, maxAvailableHeight - headerHeight);

        if (maxScrollHeight > 0 && desiredScrollHeight > maxScrollHeight) {
            final ViewGroup.LayoutParams scrollParams = scrollView.getLayoutParams();
            scrollParams.height = maxScrollHeight;
            scrollView.setLayoutParams(scrollParams);
        }
    }

    private int measureHeaderHeight(@NonNull final View content, final int popupWidth) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(popupWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int headerHeight = 0;

        final View themeToggle = content.findViewById(R.id.theme_toggle);
        if (themeToggle != null) {
            themeToggle.measure(widthSpec, heightSpec);
            headerHeight += themeToggle.getMeasuredHeight();
        }

        final View divider = content.findViewById(R.id.overflow_menu_divider);
        if (divider != null) {
            divider.measure(widthSpec, heightSpec);
            headerHeight += divider.getMeasuredHeight();
        }

        return headerHeight;
    }

    private int getMaxAvailablePopupHeight(@NonNull final View anchor) {
        final int[] anchorLocation = new int[2];
        anchor.getLocationOnScreen(anchorLocation);
        final int anchorBottom = anchorLocation[1] + anchor.getHeight();

        final View rootView = anchor.getRootView();
        final int[] rootLocation = new int[2];
        rootView.getLocationOnScreen(rootLocation);

        int bottomInset = rootView.getPaddingBottom();
        if (bottomInset == 0) {
            final WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(rootView);
            if (windowInsets != null) {
                bottomInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            }
        }

        final int usableContentBottom = rootLocation[1] + rootView.getHeight() - bottomInset;
        final int bottomMargin = dp(anchor.getContext(), 8);

        return Math.max(0, usableContentBottom - anchorBottom - bottomMargin);
    }

    private void populateMenuRows(
            @NonNull final LinearLayout container,
            @NonNull final LayoutInflater inflater,
            @NonNull final List<MenuItem> overflowItems,
            @NonNull final Callback callback) {
        container.removeAllViews();
        for (final MenuItem item : overflowItems) {
            final TextView row = (TextView) inflater.inflate(
                    R.layout.overflow_menu_item, container, false);
            final CharSequence title = item.getTitle();
            row.setText(title != null ? title : "");
            row.setOnClickListener(v -> {
                dismiss();
                callback.onMenuItemClick(item.getItemId());
            });
            container.addView(row);
        }
    }

    @NonNull
    private List<MenuItem> collectOverflowItems(@NonNull final MaterialToolbar toolbar) {
        final Menu menu = toolbar.getMenu();
        final List<MenuItem> items = new ArrayList<>();
        for (final int itemId : OVERFLOW_ITEM_IDS) {
            final MenuItem item = menu.findItem(itemId);
            if (item == null || !item.isVisible()) {
                continue;
            }
            if (itemId == R.id.menu_toggle_modes && isMenuItemOnToolbar(toolbar, itemId)) {
                continue;
            }
            items.add(item);
        }
        return items;
    }

    private boolean isMenuItemOnToolbar(@NonNull final MaterialToolbar toolbar, final int itemId) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View child = toolbar.getChildAt(i);
            if (!(child instanceof ActionMenuView actionMenuView)) {
                continue;
            }
            for (int j = 0; j < actionMenuView.getChildCount(); j++) {
                final View menuChild = actionMenuView.getChildAt(j);
                if (menuChild instanceof ActionMenuItemView actionItemView) {
                    final MenuItem item = actionItemView.getItemData();
                    if (item != null && item.getItemId() == itemId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    private View findOverflowMenuButton(@NonNull final MaterialToolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View child = toolbar.getChildAt(i);
            if (!(child instanceof ActionMenuView actionMenuView)) {
                continue;
            }
            for (int j = actionMenuView.getChildCount() - 1; j >= 0; j--) {
                final View menuChild = actionMenuView.getChildAt(j);
                if (menuChild instanceof ActionMenuItemView) {
                    continue;
                }
                if (menuChild.isClickable() && menuChild.getVisibility() == View.VISIBLE) {
                    return menuChild;
                }
            }
        }
        return null;
    }

    @NonNull
    private static Context resolveActivityContext(@NonNull Context context) {
        if (context instanceof Activity) {
            return context;
        }
        if (context instanceof ContextThemeWrapper wrapper) {
            final Context base = wrapper.getBaseContext();
            if (base != context) {
                return resolveActivityContext(base);
            }
        }
        return context;
    }

    private static int dp(@NonNull final Context context, final int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
