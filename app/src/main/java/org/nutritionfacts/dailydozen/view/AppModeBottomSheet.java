package org.nutritionfacts.dailydozen.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.activity.MainActivity;
import org.nutritionfacts.dailydozen.controller.Prefs;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            ((MainActivity) Objects.requireNonNull(getContext())).invalidateOptionsMenu();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @OnClick(R.id.button_daily_dozen_and_tweaks)
    void onDailyDozenAndTweaksClicked() {
        final Prefs prefs = Prefs.getInstance(getContext());
        prefs.setAppModeToDailyDozenAndTweaks();
        userHasMadeSelection(prefs);
    }

    @OnClick(R.id.button_daily_dozen_only)
    void onDailyDozenOnlyClicked() {
        final Prefs prefs = Prefs.getInstance(getContext());
        prefs.setAppModeToDailyDozenOnly();
        userHasMadeSelection(prefs);
    }

    private void userHasMadeSelection(final Prefs prefs) {
        prefs.setUserHasSeenOnboardingScreen();
        dismiss();
    }
}
