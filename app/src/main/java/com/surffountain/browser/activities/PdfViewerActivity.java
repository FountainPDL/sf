package com.surffountain.browser.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.surffountain.browser.R;

public class PdfViewerActivity extends AppCompatActivity {

    public static final String EXTRA_PDF_URL = "pdf_url";
    public static final String EXTRA_PDF_TITLE = "pdf_title";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String pdfUrl = getIntent().getStringExtra(EXTRA_PDF_URL);
        String title = getIntent().getStringExtra(EXTRA_PDF_TITLE);
        if (title != null) setTitle(title);

        webView = findViewById(R.id.webview_pdf);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        if (pdfUrl != null) {
            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
            webView.loadUrl(googleDocsUrl);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
