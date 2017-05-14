package org.nutritionfacts.dailydozen.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.BuildConfig;
import org.nutritionfacts.dailydozen.Common;
import org.nutritionfacts.dailydozen.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.about_welcome)
    protected TextView tvWelcome;
    @BindView(R.id.about_header)
    protected TextView tvHeader;
    @BindView(R.id.about_version)
    protected TextView tvVersion;
    @BindView(R.id.about_text)
    protected TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initActionBar();

        initHeader();

        initLinksInWelcome();
        initLinksInText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initHeader() {
        tvHeader.setText(getString(R.string.app_name));
        tvVersion.setText(getString(R.string.format_version, BuildConfig.VERSION_NAME));
    }

    private void initLinksInWelcome() {
        final String welcomeText = getString(R.string.activity_welcome_text);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(welcomeText);

        initLink(welcomeText, ssb, R.string.book_title, R.string.url_book);

        tvWelcome.setMovementMethod(LinkMovementMethod.getInstance());
        tvWelcome.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private void initLinksInText() {
        final String aboutText = TextUtils.join(Common.getLineSeparator(),
                getResources().getStringArray(R.array.about_text_lines));
        final SpannableStringBuilder ssb = new SpannableStringBuilder(aboutText);

        initLink(aboutText, ssb, R.string.name_john_slavick, R.string.url_john_slavick);
        initLink(aboutText, ssb, R.string.library_activeandroid, R.string.url_activeandroid);
        initLink(aboutText, ssb, R.string.library_android_iconify, R.string.url_android_iconify);
        initLink(aboutText, ssb, R.string.library_butterknife, R.string.url_butterknife);
        initLink(aboutText, ssb, R.string.library_caldroid, R.string.url_caldroid);
        initLink(aboutText, ssb, R.string.library_eventbus, R.string.url_eventbus);
        initLink(aboutText, ssb, R.string.library_likeanimation, R.string.url_likeanimation);
        initLink(aboutText, ssb, R.string.library_mpandroidchart, R.string.url_mpandroidchart);

        tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
        tvAbout.setText(ssb, TextView.BufferType.SPANNABLE);
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
