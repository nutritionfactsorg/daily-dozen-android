package org.nutritionfacts.dailydozen.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AppModeBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private Unbinder unbinder;

    public static AppModeBottomSheet newInstance() {
        return new AppModeBottomSheet();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_app_mode, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.button_daily_dozen_and_tweaks)
    public void onDailyDozenAndTweaksClicked() {
        final Prefs prefs = Prefs.getInstance(getContext());
        prefs.setAppModeToDailyDozenAndTweaks();
        userHasMadeSelection(prefs);
    }

    @OnClick(R.id.button_daily_dozen_only)
    public void onDailyDozenOnlyClicked() {
        final Prefs prefs = Prefs.getInstance(getContext());
        prefs.setAppModeToDailyDozenOnly();
        userHasMadeSelection(prefs);
    }

    private void userHasMadeSelection(final Prefs prefs) {
        prefs.setUserHasSeenOnboardingScreen();
        dismiss();
    }
}
