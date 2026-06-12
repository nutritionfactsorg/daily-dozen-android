package org.nutritionfacts.dailydozen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import org.nutritionfacts.dailydozen.R;

public class ThemeToggleView extends FrameLayout {
    private static final long ANIM_DURATION_MS = 150L;
    private static final float INACTIVE_ICON_ALPHA = 0.45f;

    public interface OnThemeToggleListener {
        void onThemeToggle();
    }

    private View track;
    private ImageView moon;
    private ImageView sun;
    private ImageView thumb;

    private float thumbTravelPx;
    private boolean isDarkMode;
    private boolean animating;

    @Nullable
    private OnThemeToggleListener listener;

    public ThemeToggleView(Context context) {
        super(context);
        init(context);
    }

    public ThemeToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ThemeToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_theme_toggle, this, true);
        track = findViewById(R.id.theme_toggle_track);
        moon = findViewById(R.id.theme_toggle_moon);
        sun = findViewById(R.id.theme_toggle_sun);
        thumb = findViewById(R.id.theme_toggle_thumb);

        setOnClickListener(v -> {
            if (animating) {
                return;
            }
            setDarkMode(!isDarkMode, true);
            if (listener != null) {
                postDelayed(listener::onThemeToggle, ANIM_DURATION_MS);
            }
        });
    }

    public void setOnThemeToggleListener(@Nullable OnThemeToggleListener listener) {
        this.listener = listener;
    }

    public void setDarkMode(final boolean darkMode, final boolean animate) {
        isDarkMode = darkMode;
        updateIconAlphas();

        if (thumbTravelPx <= 0f) {
            thumb.setTranslationX(0f);
            return;
        }

        final float targetTranslation = darkMode ? 0f : thumbTravelPx;
        thumb.animate().cancel();
        if (animate) {
            animating = true;
            thumb.animate()
                    .translationX(targetTranslation)
                    .setDuration(ANIM_DURATION_MS)
                    .withEndAction(() -> animating = false)
                    .start();
        } else {
            animating = false;
            thumb.setTranslationX(targetTranslation);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        thumbTravelPx = track.getWidth() - thumb.getWidth() - track.getPaddingLeft() - track.getPaddingRight();
        if (thumbTravelPx < 0f) {
            thumbTravelPx = 0f;
        }
        if (!animating) {
            thumb.setTranslationX(isDarkMode ? 0f : thumbTravelPx);
        }
    }

    private void updateIconAlphas() {
        moon.setAlpha(isDarkMode ? 1f : INACTIVE_ICON_ALPHA);
        sun.setAlpha(isDarkMode ? INACTIVE_ICON_ALPHA : 1f);
    }
}
