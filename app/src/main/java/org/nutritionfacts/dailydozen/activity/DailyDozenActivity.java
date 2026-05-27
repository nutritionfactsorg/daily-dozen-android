package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;

public class DailyDozenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureSystemBars();
        applyWindowInsets(findViewById(android.R.id.content));
    }

    private void configureSystemBars() {
        final Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, !Prefs.getInstance(this).isDarkMode());

        if (Prefs.getInstance(this).isDarkMode()) {
            return;
        }

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        final WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }

    protected void applyWindowInsets(ViewGroup viewGroup) {
        ViewCompat.setOnApplyWindowInsetsListener(viewGroup,
                (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    // Light mode: framework lays out below the status/action bars (decorFitsSystemWindows).
                    // Dark mode: keep top inset for edge-to-edge layout.
                    final int topInset = Prefs.getInstance(this).isDarkMode() ? insets.top : 0;
                    v.setPadding(insets.left, topInset, insets.right, insets.bottom);
                    return WindowInsetsCompat.CONSUMED;
                });
    }
}
