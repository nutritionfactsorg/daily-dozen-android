package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DailyDozenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyWindowInsets(findViewById(android.R.id.content));
    }

    protected void applyWindowInsets(ViewGroup viewGroup) {
        ViewCompat.setOnApplyWindowInsetsListener(viewGroup,
                (v, windowInsets) -> {
                    Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

                    // Return CONSUMED if you don't want the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                });
    }
}
