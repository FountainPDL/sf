package com.surffountain.browser.activities;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.databinding.ActivityMainBinding;
import com.surffountain.browser.fragments.AddressBarFragment;
import com.surffountain.browser.fragments.HomeFragment;
import com.surffountain.browser.fragments.TabsFragment;
import com.surffountain.browser.models.BrowserTab;
import com.surffountain.browser.security.PasswordManager;
import com.surffountain.browser.settings.SettingsManager;
import com.surffountain.browser.utils.UrlUtils;
import com.surffountain.browser.viewmodels.BrowserViewModel;
import com.surffountain.browser.webview.SurfWebChromeClient;
import com.surffountain.browser.webview.SurfWebView;
import com.surffountain.browser.webview.SurfWebViewClient;
import com.surffountain.browser.webview.TabManager;

public class MainActivity extends AppCompatActivity implements
        SurfWebView.PageListener,
        SurfWebChromeClient.ChromeListener,
        AddressBarFragment.AddressBarListener {

    private ActivityMainBinding binding;
    private BrowserViewModel viewModel;
    private TabManager tabManager;
    private SettingsManager settingsManager;
    private SurfWebView currentWebView;
    private SurfWebViewClient webViewClient;
    private SurfWebChromeClient webChromeClient;

    private ValueCallback<Uri[]> fileChooserCallback;
    private boolean isFullscreen = false;
    private static final int FILE_CHOOSER_REQUEST = 1001;
    private static final int PERMISSION_REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tabManager = TabManager.getInstance(this);
        settingsManager = SurfFountainApp.getInstance().getSettingsManager();
        viewModel = new ViewModelProvider(this).get(BrowserViewModel.class);

        setupWebView();
        setupObservers();
        setupAddressBar();
        setupBottomBar();
        handleIntent(getIntent());

        // Restore tabs from last session
        if (savedInstanceState == null) {
            viewModel.restoreSession(this);
        }
    }

    private void setupWebView() {
        webViewClient = new SurfWebViewClient(this);
        webViewClient.setPageListener(this);

        webChromeClient = new SurfWebChromeClient(this);
        webChromeClient.setChromeListener(this);
    }

    private void setupObservers() {
        tabManager.getActiveTabLiveData().observe(this, tab -> {
            if (tab == null) {
                showHomePage();
                return;
            }
            loadTabInWebView(tab);
            updateAddressBar(tab.getUrl());
            updateTabCount();
        });

        tabManager.getTabsLiveData().observe(this, tabs -> {
            updateTabCount();
        });

        viewModel.getRestoredTabs().observe(this, tabs -> {
            if (tabs != null && !tabs.isEmpty()) {
                for (String url : tabs) {
                    BrowserTab tab = tabManager.createTab(url, false);
                }
                tabManager.setActiveTab(tabManager.getTabsLiveData().getValue().get(0));
            } else {
                openNewTab(false);
            }
        });
    }

    private void loadTabInWebView(BrowserTab tab) {
        binding.webViewContainer.removeAllViews();

        SurfWebView webView = tabManager.getWebView(tab.getId());
        if (webView == null) {
            webView = new SurfWebView(this);
            webView.setWebViewClient(webViewClient);
            webView.setWebChromeClient(webChromeClient);
            webView.setPageListener(this);

            if (tab.isIncognito()) {
                webView.enableIncognito();
            }

            tabManager.registerWebView(tab.getId(), webView);

            if (!tab.isHomePage()) {
                webView.loadUrl(tab.getUrl());
            }
        }

        currentWebView = webView;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        binding.webViewContainer.addView(webView, params);

        if (tab.isHomePage()) {
            showHomePage();
        } else {
            hideHomePage();
        }
    }

    private void setupAddressBar() {
        AddressBarFragment fragment = AddressBarFragment.newInstance();
        fragment.setListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.address_bar_container, fragment)
                .commit();
    }

    private void setupBottomBar() {
        binding.btnBack.setOnClickListener(v -> {
            if (currentWebView != null && currentWebView.canGoBack()) {
                currentWebView.goBack();
            }
        });

        binding.btnForward.setOnClickListener(v -> {
            if (currentWebView != null && currentWebView.canGoForward()) {
                currentWebView.goForward();
            }
        });

        binding.btnHome.setOnClickListener(v -> {
            showHomePage();
        });

        binding.btnTabs.setOnClickListener(v -> {
            showTabsSwitcher();
        });

        binding.btnMenu.setOnClickListener(v -> {
            showBrowserMenu();
        });
    }

    // AddressBarFragment.AddressBarListener implementation
    @Override
    public void onUrlSubmitted(String input) {
        String url = UrlUtils.buildUrl(input, settingsManager.getSearchEngine());
        navigateTo(url);
    }

    @Override
    public void onVoiceSearch() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 2001);
    }

    @Override
    public void onNewIncognitoTab() {
        openNewTab(true);
    }

    @Override
    public void onSearchEngineChanged(String engineId) {
        settingsManager.setSearchEngine(engineId);
    }

    // SurfWebView.PageListener implementation
    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        runOnUiThread(() -> {
            BrowserTab active = tabManager.getActiveTab();
            if (active != null) {
                active.setLoading(true);
                active.setUrl(url);
            }
            binding.progressBar.setVisibility(View.VISIBLE);
            updateAddressBar(url);
            updateNavButtons();
        });
    }

    @Override
    public void onPageFinished(String url) {
        runOnUiThread(() -> {
            BrowserTab active = tabManager.getActiveTab();
            if (active != null) {
                active.setLoading(false);
                active.setUrl(url);
            }
            binding.progressBar.setVisibility(View.GONE);
            viewModel.saveToHistory(url, active != null ? active.getTitle() : "");
            updateNavButtons();
            // Auto-save passwords
            checkPasswordSave(url);
        });
    }

    @Override
    public void onProgressChanged(int progress) {
        runOnUiThread(() -> {
            binding.progressBar.setProgress(progress);
        });
    }

    @Override
    public void onTitleReceived(String title) {
        runOnUiThread(() -> {
            BrowserTab active = tabManager.getActiveTab();
            if (active != null) active.setTitle(title);
        });
    }

    @Override
    public void onFaviconReceived(Bitmap favicon) {
        BrowserTab active = tabManager.getActiveTab();
        if (active != null) active.setFavicon(favicon);
    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY) {
        boolean scrollingDown = y > oldY;
        if (scrollingDown) {
            binding.bottomBar.animate().translationY(binding.bottomBar.getHeight()).setDuration(200).start();
        } else {
            binding.bottomBar.animate().translationY(0).setDuration(200).start();
        }
    }

    @Override
    public void onSwipeLeft() {
        if (currentWebView != null && currentWebView.canGoForward()) {
            currentWebView.goForward();
        }
    }

    @Override
    public void onSwipeRight() {
        if (currentWebView != null && currentWebView.canGoBack()) {
            currentWebView.goBack();
        }
    }

    // SurfWebChromeClient.ChromeListener implementation
    @Override
    public void onEnterFullscreen(View customView) {
        isFullscreen = true;
        binding.fullscreenContainer.addView(customView);
        binding.fullscreenContainer.setVisibility(View.VISIBLE);
        binding.webViewContainer.setVisibility(View.GONE);
        binding.addressBarContainer.setVisibility(View.GONE);
        binding.bottomBar.setVisibility(View.GONE);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    public void onExitFullscreen() {
        isFullscreen = false;
        binding.fullscreenContainer.removeAllViews();
        binding.fullscreenContainer.setVisibility(View.GONE);
        binding.webViewContainer.setVisibility(View.VISIBLE);
        binding.addressBarContainer.setVisibility(View.VISIBLE);
        binding.bottomBar.setVisibility(View.VISIBLE);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
    }

    @Override
    public void onNewWindowRequested(WebView newWebView) {
        BrowserTab tab = tabManager.createTab("about:blank", false);
        tabManager.setActiveTab(tab);
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceId, android.webkit.ConsoleMessage.MessageLevel level) {
        // Console messages captured for DevTools
    }

    @Override
    public void onFileChooserRequest(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams params) {
        fileChooserCallback = filePathCallback;
        Intent chooser = params.createIntent();
        startActivityForResult(Intent.createChooser(chooser, "Select File"), FILE_CHOOSER_REQUEST);
    }

    @Override
    public void onGeolocationPermissionRequest(String origin, android.webkit.GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
    }

    private void navigateTo(String url) {
        BrowserTab active = tabManager.getActiveTab();
        if (active == null) {
            active = tabManager.createTab(url, false);
            tabManager.setActiveTab(active);
        } else {
            active.setUrl(url);
            if (currentWebView != null) {
                currentWebView.loadUrl(url);
                hideHomePage();
            }
        }
    }

    private void openNewTab(boolean incognito) {
        BrowserTab tab = tabManager.createTab("surf://home", incognito);
        tabManager.setActiveTab(tab);
        showHomePage();
    }

    private void showHomePage() {
        HomeFragment home = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
        if (home == null) {
            home = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_container, home, "home")
                    .commit();
        }
        binding.homeContainer.setVisibility(View.VISIBLE);
        binding.webViewContainer.setVisibility(View.GONE);
    }

    private void hideHomePage() {
        binding.homeContainer.setVisibility(View.GONE);
        binding.webViewContainer.setVisibility(View.VISIBLE);
    }

    private void showTabsSwitcher() {
        TabsFragment tabsFragment = TabsFragment.newInstance();
        tabsFragment.show(getSupportFragmentManager(), "tabs");
    }

    private void showBrowserMenu() {
        BottomSheetDialog menu = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        View menuView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        setupMenuActions(menuView, menu);
        menu.setContentView(menuView);
        menu.show();
    }

    private void setupMenuActions(View menuView, BottomSheetDialog menu) {
        menuView.findViewById(R.id.menu_bookmarks).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, BookmarksActivity.class));
        });
        menuView.findViewById(R.id.menu_history).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, HistoryActivity.class));
        });
        menuView.findViewById(R.id.menu_downloads).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, DownloadsActivity.class));
        });
        menuView.findViewById(R.id.menu_privacy).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, PrivacyCenterActivity.class));
        });
        menuView.findViewById(R.id.menu_settings).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, SettingsActivity.class));
        });
        menuView.findViewById(R.id.menu_new_tab).setOnClickListener(v -> {
            menu.dismiss();
            openNewTab(false);
        });
        menuView.findViewById(R.id.menu_incognito).setOnClickListener(v -> {
            menu.dismiss();
            openNewTab(true);
        });
        menuView.findViewById(R.id.menu_ai).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, AiAssistantActivity.class));
        });
        menuView.findViewById(R.id.menu_reader_mode).setOnClickListener(v -> {
            menu.dismiss();
            launchReaderMode();
        });
        menuView.findViewById(R.id.menu_share).setOnClickListener(v -> {
            menu.dismiss();
            sharePage();
        });
        menuView.findViewById(R.id.menu_add_bookmark).setOnClickListener(v -> {
            menu.dismiss();
            addCurrentPageBookmark();
        });
        menuView.findViewById(R.id.menu_desktop_mode).setOnClickListener(v -> {
            menu.dismiss();
            if (currentWebView != null) currentWebView.toggleDesktopMode();
        });
        menuView.findViewById(R.id.menu_find_in_page).setOnClickListener(v -> {
            menu.dismiss();
            showFindInPage();
        });
        menuView.findViewById(R.id.menu_screenshot).setOnClickListener(v -> {
            menu.dismiss();
            takeScreenshot();
        });
        menuView.findViewById(R.id.menu_dev_tools).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, DevToolsActivity.class));
        });
        menuView.findViewById(R.id.menu_wallet).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, WalletActivity.class));
        });
        menuView.findViewById(R.id.menu_vpn).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, VpnActivity.class));
        });
        menuView.findViewById(R.id.menu_qr).setOnClickListener(v -> {
            menu.dismiss();
            startActivity(new Intent(this, QrActivity.class));
        });
    }

    private void launchReaderMode() {
        if (currentWebView == null) return;
        currentWebView.extractPageText(text -> {
            Intent intent = new Intent(this, ReaderModeActivity.class);
            intent.putExtra("text", text);
            intent.putExtra("url", currentWebView.getUrl());
            intent.putExtra("title", currentWebView.getTitle());
            startActivity(intent);
        });
    }

    private void sharePage() {
        if (currentWebView == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentWebView.getUrl());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentWebView.getTitle());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void addCurrentPageBookmark() {
        if (currentWebView == null) return;
        viewModel.addBookmark(currentWebView.getUrl(), currentWebView.getTitle());
        Toast.makeText(this, R.string.bookmark_added, Toast.LENGTH_SHORT).show();
    }

    private void showFindInPage() {
        binding.findInPageBar.setVisibility(View.VISIBLE);
        binding.findInPageInput.requestFocus();
        if (currentWebView != null) {
            binding.findInPageInput.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentWebView.findAllAsync(s.toString());
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
        }
        binding.findInPageClose.setOnClickListener(v -> {
            binding.findInPageBar.setVisibility(View.GONE);
            if (currentWebView != null) currentWebView.clearMatches();
        });
    }

    private void takeScreenshot() {
        if (currentWebView == null) return;
        Bitmap screenshot = currentWebView.captureThumbnail(
                currentWebView.getWidth(), currentWebView.getHeight());
        viewModel.saveScreenshot(screenshot, this);
        Toast.makeText(this, "Screenshot saved", Toast.LENGTH_SHORT).show();
    }

    private void checkPasswordSave(String url) {
        if (!settingsManager.isSavePasswordsEnabled()) return;
        // Inject password detection JS
        if (currentWebView != null) {
            currentWebView.evaluateJavascript(
                "(function(){var f=document.querySelector('form');if(!f)return null;" +
                "var u=f.querySelector('input[type=email],input[type=text]');" +
                "var p=f.querySelector('input[type=password]');" +
                "if(u&&p)return JSON.stringify({username:u.value,password:p.value});return null;})();",
                value -> {
                    if (value != null && !value.equals("null") && !value.isEmpty()) {
                        viewModel.promptPasswordSave(url, value);
                    }
                }
            );
        }
    }

    private void updateAddressBar(String url) {
        AddressBarFragment frag = (AddressBarFragment) getSupportFragmentManager().findFragmentById(R.id.address_bar_container);
        if (frag != null) frag.setUrl(url);
    }

    private void updateTabCount() {
        int count = tabManager.getTabCount();
        binding.btnTabs.setText(count > 99 ? "99+" : String.valueOf(count));
    }

    private void updateNavButtons() {
        if (currentWebView != null) {
            binding.btnBack.setAlpha(currentWebView.canGoBack() ? 1f : 0.4f);
            binding.btnForward.setAlpha(currentWebView.canGoForward() ? 1f : 0.4f);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            webChromeClient.exitFullscreen();
            return;
        }
        if (binding.findInPageBar.getVisibility() == View.VISIBLE) {
            binding.findInPageBar.setVisibility(View.GONE);
            return;
        }
        if (currentWebView != null && currentWebView.canGoBack()) {
            currentWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && currentWebView != null && currentWebView.canGoBack()) {
            currentWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST) {
            if (fileChooserCallback != null) {
                Uri[] results = resultCode == RESULT_OK && data != null ?
                        new Uri[]{data.getData()} : null;
                fileChooserCallback.onReceiveValue(results);
                fileChooserCallback = null;
            }
        } else if (requestCode == 2001 && resultCode == RESULT_OK && data != null) {
            // Voice search result
            java.util.ArrayList<String> results = data.getStringArrayListExtra(
                    android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                onUrlSubmitted(results.get(0));
            }
        }
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Enter PiP for video
        if (isFullscreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(new Rational(16, 9))
                    .build();
            enterPictureInPictureMode(params);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentWebView != null) currentWebView.onPause();
        settingsManager.saveStats();
        viewModel.saveSession(tabManager, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentWebView != null) currentWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (settingsManager.isClearOnExitEnabled()) {
            if (currentWebView != null) currentWebView.clearAllData();
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        Uri data = intent.getData();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            openNewTab(false);
            navigateTo(data.toString());
        } else if (Intent.ACTION_SEND.equals(action)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (text != null) {
                openNewTab(false);
                navigateTo(text);
            }
        }
    }
}
