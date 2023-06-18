package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.ActivityFaqBinding;

import io.noties.markwon.Markwon;

public class FaqActivity extends AppCompatActivity {
    private ActivityFaqBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        final Markwon markwon = Markwon.create(this);
        markwon.setMarkdown(binding.faqScalingResponse, getString(R.string.faq_scaling_response));
        markwon.setMarkdown(binding.faqSupplementsResponse, getString(R.string.faq_supplements_response));
    }
}
