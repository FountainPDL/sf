package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.tabs.TabLayout;
import com.surffountain.browser.R;
import com.surffountain.browser.adapters.ConsoleLogAdapter;
import com.surffountain.browser.databinding.ActivityDevToolsBinding;
import com.surffountain.browser.webview.TabManager;

public class DevToolsActivity extends AppCompatActivity {

    private ActivityDevToolsBinding binding;
    private TabManager tabManager;
    private ConsoleLogAdapter consoleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDevToolsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Developer Tools");
        }

        tabManager = TabManager.getInstance(this);
        setupTabs();
        setupActions();
        loadPageSource();
    }

    private void setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Source"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Console"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("User Agent"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Network"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: showSource(); break;
                    case 1: showConsole(); break;
                    case 2: showUserAgent(); break;
                    case 3: showNetwork(); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadPageSource() {
        com.surffountain.browser.models.BrowserTab active = tabManager.getActiveTab();
        if (active == null) return;
        com.surffountain.browser.webview.SurfWebView wv = tabManager.getWebView(active.getId());
        if (wv == null) return;
        wv.evaluateJavascript("document.documentElement.outerHTML", source -> {
            runOnUiThread(() -> {
                if (source != null) {
                    String cleaned = source.replace("\\n", "\n").replace("\\\"", "\"");
                    if (cleaned.startsWith("\"")) cleaned = cleaned.substring(1, cleaned.length() - 1);
                    binding.tvSource.setText(cleaned);
                }
            });
        });
    }

    private void showSource() {
        binding.tvSource.setVisibility(android.view.View.VISIBLE);
        binding.recyclerConsole.setVisibility(android.view.View.GONE);
        binding.layoutUserAgent.setVisibility(android.view.View.GONE);
    }

    private void showConsole() {
        binding.tvSource.setVisibility(android.view.View.GONE);
        binding.recyclerConsole.setVisibility(android.view.View.VISIBLE);
        binding.layoutUserAgent.setVisibility(android.view.View.GONE);
        consoleAdapter = new ConsoleLogAdapter();
        binding.recyclerConsole.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerConsole.setAdapter(consoleAdapter);
    }

    private void showUserAgent() {
        binding.tvSource.setVisibility(android.view.View.GONE);
        binding.recyclerConsole.setVisibility(android.view.View.GONE);
        binding.layoutUserAgent.setVisibility(android.view.View.VISIBLE);

        com.surffountain.browser.models.BrowserTab active = tabManager.getActiveTab();
        if (active != null) {
            com.surffountain.browser.webview.SurfWebView wv = tabManager.getWebView(active.getId());
            if (wv != null) {
                binding.etUserAgent.setText(wv.getSettings().getUserAgentString());
                binding.btnApplyUa.setOnClickListener(v -> {
                    String ua = binding.etUserAgent.getText().toString();
                    wv.setCustomUserAgent(ua);
                    wv.reload();
                });
            }
        }
    }

    private void showNetwork() {
        binding.tvSource.setVisibility(android.view.View.VISIBLE);
        binding.tvSource.setText("Network monitoring requires Chromium DevTools Protocol.\nEnable via chrome://inspect on desktop.");
    }

    private void setupActions() {
        binding.btnCopySource.setOnClickListener(v -> {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("source", binding.tvSource.getText()));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
