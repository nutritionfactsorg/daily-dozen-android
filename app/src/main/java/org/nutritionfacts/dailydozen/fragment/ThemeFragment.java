package org.nutritionfacts.dailydozen.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.controller.Prefs;


public class ThemeFragment extends DialogFragment {

    public ThemeFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.theme)
                .setSingleChoiceItems(R.array.theme_options, Prefs.getInstance(getContext()).getThemePref(), (dialogInterface, i) -> {
                    switch (i) {
                        case 1:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            persistSetting(AppCompatDelegate.MODE_NIGHT_NO);
                            dismiss();
                            return;
                        case 2:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            persistSetting(AppCompatDelegate.MODE_NIGHT_YES);
                            dismiss();
                            return;
                        default:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            persistSetting(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            dismiss();
                    }
                });
        return builder.create();
    }

    private void persistSetting(int theme) {
        // sanitize MODE_NIGHT_FOLLOW_SYSTEM value for setSingleChoiceItems method to
        // highlight correct option in AlertDialog
        if (theme < 0) {
            theme = 0;
        }
        Prefs.getInstance(getContext()).setThemePref(theme);
    }
}