package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.nutritionfacts.dailydozen.BuildConfig;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initActionBar();

        initHeader();

        initLinksInWelcome();
        initLinksInText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initHeader() {
        binding.aboutHeader.setText(getString(R.string.app_name));
        binding.aboutVersion.setText(getString(R.string.format_version, BuildConfig.VERSION_NAME));
    }

    private void initLinksInWelcome() {
        final String welcomeText = getString(R.string.activity_welcome_text);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(welcomeText);

        initLink(welcomeText, ssb, R.string.title_how_not_to_die, R.string.url_how_not_to_die);
        initLink(welcomeText, ssb, R.string.title_how_not_to_diet, R.string.url_how_not_to_diet);

        binding.aboutWelcome.setMovementMethod(LinkMovementMethod.getInstance());
        binding.aboutWelcome.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private void initLinksInText() {
        final String aboutText = TextUtils.join(System.lineSeparator(),
                getResources().getStringArray(R.array.about_text_lines));
        final SpannableStringBuilder ssb = new SpannableStringBuilder(aboutText);

        initLink(aboutText, ssb, R.string.name_john_slavick, R.string.url_john_slavick);
        initLink(aboutText, ssb, R.string.library_activeandroid, R.string.url_activeandroid);
        initLink(aboutText, ssb, R.string.library_android_iconify, R.string.url_android_iconify);
        initLink(aboutText, ssb, R.string.library_eventbus, R.string.url_eventbus);
        initLink(aboutText, ssb, R.string.library_likeanimation, R.string.url_likeanimation);
        initLink(aboutText, ssb, R.string.library_mpandroidchart, R.string.url_mpandroidchart);

        binding.aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        binding.aboutText.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private void initLink(final String textToSearch, final SpannableStringBuilder ssb, final int textToFindId, final int urlId) {
        final String textToFind = getString(textToFindId);

        final int startIndex = textToSearch.indexOf(textToFind);

        if (startIndex >= 0) {
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Common.openUrlInExternalBrowser(AboutActivity.this, urlId);
                }
            }, startIndex, startIndex + textToFind.length(), 0);
        }
    }
}
