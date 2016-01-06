package org.slavick.dailydozen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.slavick.dailydozen.BuildConfig;
import org.slavick.dailydozen.R;
import org.slavick.dailydozen.widget.CardViewHeader;

public class AboutActivity extends AppCompatActivity {
    protected CardViewHeader cvHeader;
    protected TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        cvHeader = (CardViewHeader) findViewById(R.id.about_header);
        tvAbout = (TextView) findViewById(R.id.about_text);

        initActionBar();

        initHeader();
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
        cvHeader.setHeader(getString(R.string.app_name));
        cvHeader.setSubHeader(String.format("version %s", BuildConfig.VERSION_NAME));
    }

    private void initLinksInText() {
        final String aboutText = getString(R.string.about_text);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(aboutText);

        initLink(aboutText, ssb, R.string.book_title, R.string.url_book);
        initLink(aboutText, ssb, R.string.nutritionfacts_title, R.string.url_nutritionfacts);
        initLink(aboutText, ssb, R.string.my_name, R.string.url_my_website);
        initLink(aboutText, ssb, R.string.library_activeandroid, R.string.url_activeandroid);
        initLink(aboutText, ssb, R.string.library_caldroid, R.string.url_caldroid);

        tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
        tvAbout.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private void initLink(final String textToSearch, final SpannableStringBuilder ssb, final int textToFindId, final int urlId) {
        final String textToFind = getString(textToFindId);
        final String url = getString(urlId);

        final int startIndex = textToSearch.indexOf(textToFind);

        if (startIndex >= 0) {
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }, startIndex, startIndex + textToFind.length(), 0);
        }
    }
}
