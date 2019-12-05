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

public class TweakInfoActivity extends InfoActivity {
    @BindView(R.id.tweak_info_image)
    protected ImageView ivTweak;
    @BindView(R.id.tweak_text)
    protected TextView tvTweakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweak_info);
        ButterKnife.bind(this);

        displayTweakInfo();
    }

    private void displayTweakInfo() {
        final Tweak tweak = getTweak();

        if (tweak != null && !TextUtils.isEmpty(tweak.getName())) {
            initImage(tweak.getName());
            initTweakText(tweak.getName());
        }
    }

    private void initImage(String foodName) {
        Common.loadImage(this, ivTweak, FoodInfo.getFoodImage(foodName));
    }

    private void initTweakText(String tweakName) {
        tvTweakText.setText(FoodInfo.getTweakText(tweakName));
    }
}
