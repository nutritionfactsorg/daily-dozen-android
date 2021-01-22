package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.model.FoodInfo;
import org.nutritionfacts.dailydozen.model.Tweak;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TweakInfoActivity extends InfoActivity {
    @BindView(R.id.tweak_info_image)
    protected ImageView ivTweak;
    @BindView(R.id.tweak_short)
    protected TextView tvTweakShort;
    @BindView(R.id.tweak_text)
    protected TextView tvTweakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getTweak() == null) {
            Timber.e("Could not open activity: getTweak returned null");
            finish();
            return;
        }

        setContentView(R.layout.activity_tweak_info);
        ButterKnife.bind(this);

        displayTweakInfo();
    }

    private void displayTweakInfo() {
        final Tweak tweak = getTweak();

        if (tweak != null && !TextUtils.isEmpty(tweak.getName())) {
            initImage(tweak.getName());
            initTweakShort(tweak.getName());
            initTweakText(tweak.getName());
        }
    }

    private void initImage(String tweakName) {
        Common.loadImage(this, ivTweak, FoodInfo.getTweakImage(tweakName));
    }

    private void initTweakShort(String tweakName) {
        tvTweakShort.setText(FoodInfo.getTweakShort(tweakName));
    }

    private void initTweakText(String tweakName) {
        tvTweakText.setText(FoodInfo.getTweakText(tweakName));
    }
}
