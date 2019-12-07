package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.nutritionfacts.dailydozen.Args;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        setTitle(getString(R.string.app_name));
    }

    @OnClick(R.id.button_daily_dozen_and_tweaks)
    public void onDailyDozenAndTweaksClicked() {
        final Prefs prefs = Prefs.getInstance(this);
        prefs.setAppModeToDailyDozenAndTweaks();
        userHasMadeSelection(prefs);
    }

    @OnClick(R.id.button_daily_dozen_only)
    public void onDailyDozenOnlyClicked() {
        final Prefs prefs = Prefs.getInstance(this);
        prefs.setAppModeToDailyDozenOnly();
        userHasMadeSelection(prefs);
    }

    private void userHasMadeSelection(final Prefs prefs) {
        prefs.setUserHasSeenOnboardingScreen();
        setResult(Args.ONBOARDING_SCREEN);
        finish();
    }
}
