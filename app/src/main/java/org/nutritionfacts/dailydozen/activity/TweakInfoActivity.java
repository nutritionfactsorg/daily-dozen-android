package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.text.TextUtils;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.ActivityTweakInfoBinding;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;

import timber.log.Timber;

public class TweakInfoActivity extends InfoActivity {
    private ActivityTweakInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getTweak() == null) {
            Timber.e("Could not open activity: getTweak returned null");
            finish();
            return;
        }

        binding = ActivityTweakInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        displayTweakInfo();
    }

    private void displayTweakInfo() {
        setTitle(R.string.about_tweak);

        final Tweak tweak = getTweak();

        if (tweak != null && !TextUtils.isEmpty(tweak.getName())) {
            initImage(tweak.getName());
            initTweakShort(tweak.getName());
            initTweakText(tweak.getName());
        }
    }

    private void initImage(String tweakName) {
        Common.loadImage(this, binding.tweakInfoImage, FoodInfo.getTweakImage(tweakName));
    }

    private void initTweakShort(String tweakName) {
        binding.tweakShort.setText(FoodInfo.getTweakShort(tweakName));
    }

    private void initTweakText(String tweakName) {
        binding.tweakText.setText(FoodInfo.getTweakText(tweakName));
    }
}
