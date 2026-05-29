package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;

public class DailyDozenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEdgeToEdgeForTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean shouldShowUpNavigation() {
        return !(this instanceof MainActivity);
    }

    private void setupToolbar(MaterialToolbar toolbar, boolean showUpNavigation) {
        setSupportActionBar(toolbar);
        applyStatusBarInsets(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && getTitle() != null) {
            actionBar.setTitle(getTitle());
        }

        if (showUpNavigation) {
            enableUpNavigation();
        }
    }

    private void maybeSetupToolbar(View root) {
        if (root == null) {
            return;
        }

        final MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar, shouldShowUpNavigation());
        }
    }

    private void applyStatusBarInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view,
                (v, windowInsets) -> {
                    Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
                    v.setPadding(v.getPaddingLeft(), statusBars.top, v.getPaddingRight(), v.getPaddingBottom());
                    return WindowInsetsCompat.CONSUMED;
                });
        ViewCompat.requestApplyInsets(view);
    }

    private void enableUpNavigation() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onContentViewSet(getContentRoot());
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onContentViewSet(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        onContentViewSet(view);
    }

    private void onContentViewSet(View root) {
        applyWindowInsetsToContentRoot(root);
        maybeSetupToolbar(root);
    }

    private View getContentRoot() {
        final ViewGroup content = findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return content;
        }
        return content.getChildAt(0);
    }

    private void enableEdgeToEdgeForTheme() {
        if (Prefs.getInstance(this).isDarkMode()) {
            EdgeToEdge.enable(this);
            return;
        }

        final int statusBarScrim = ContextCompat.getColor(this, R.color.colorPrimary);
        final int lightScrim = ContextCompat.getColor(this, android.R.color.white);
        final int darkScrim = ContextCompat.getColor(this, android.R.color.black);
        EdgeToEdge.enable(
                this,
                SystemBarStyle.dark(statusBarScrim),
                SystemBarStyle.light(lightScrim, darkScrim));
    }

    private void applyWindowInsetsToContentRoot(View root) {
        if (root == null) {
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(root,
                (v, windowInsets) -> {
                    Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                    Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
                    v.setPadding(
                            systemBars.left,
                            0,
                            systemBars.right,
                            Math.max(systemBars.bottom, ime.bottom));
                    return windowInsets;
                });
        ViewCompat.requestApplyInsets(root);
    }
}
