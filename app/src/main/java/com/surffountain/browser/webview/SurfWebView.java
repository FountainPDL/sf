package com.surffountain.browser.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.settings.SettingsManager;

public class SurfWebView extends WebView {

    public interface PageListener {
        void onPageStarted(String url, Bitmap favicon);
        void onPageFinished(String url);
        void onProgressChanged(int progress);
        void onTitleReceived(String title);
        void onFaviconReceived(Bitmap favicon);
        void onScrollChanged(int x, int y, int oldX, int oldY);
        void onSwipeLeft();
        void onSwipeRight();
    }

    private PageListener pageListener;
    private GestureDetector gestureDetector;
    private boolean isMuted = false;
    private String customUserAgent;
    private boolean isDesktopMode = false;

    private static final String MOBILE_UA = "Mozilla/5.0 (Linux; Android 14; Pixel 8) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36";
    private static final String DESKTOP_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    public SurfWebView(Context context) {
        super(context);
        init(context);
    }

    public SurfWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SurfWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setupSettings();
        setupGestureDetector(context);
        setupCookies();
    }

    private void setupSettings() {
        WebSettings settings = getSettings();
        SettingsManager sm = SurfFountainApp.getInstance().getSettingsManager();

        // JavaScript
        settings.setJavaScriptEnabled(sm.isJavaScriptEnabled());

        // Media
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setLoadsImagesAutomatically(true);

        // HTML5
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(sm.isLocationEnabled());

        // Zoom
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Cache
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);

        // Viewport
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Mixed content (HTTPS upgrade handled by SurfShield)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        // User Agent
        settings.setUserAgentString(MOBILE_UA);

        // Text encoding
        settings.setDefaultTextEncodingName("UTF-8");

        // Scrollbar
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        setOverScrollMode(OVER_SCROLL_NEVER);

        // Hardware acceleration
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void setupGestureDetector(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (pageListener != null) {
                        if (diffX > 0) pageListener.onSwipeRight();
                        else pageListener.onSwipeLeft();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setupCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        SettingsManager sm = SurfFountainApp.getInstance().getSettingsManager();
        cookieManager.setAcceptCookie(sm.isCookiesEnabled());
        cookieManager.setAcceptThirdPartyCookies(this, sm.isThirdPartyCookiesEnabled());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (pageListener != null) {
            pageListener.onScrollChanged(x, y, oldX, oldY);
        }
    }

    public void setPageListener(PageListener listener) {
        this.pageListener = listener;
    }

    public PageListener getPageListener() {
        return pageListener;
    }

    public void toggleDesktopMode() {
        isDesktopMode = !isDesktopMode;
        getSettings().setUserAgentString(isDesktopMode ? DESKTOP_UA : MOBILE_UA);
        reload();
    }

    public boolean isDesktopMode() {
        return isDesktopMode;
    }

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        // Inject JS to mute/unmute all media elements
        String script = "document.querySelectorAll('video,audio').forEach(function(el){ el.muted=" + muted + "; });";
        evaluateJavascript(script, null);
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setCustomUserAgent(String ua) {
        this.customUserAgent = ua;
        if (ua != null && !ua.isEmpty()) {
            getSettings().setUserAgentString(ua);
        }
    }

    public void enableIncognito() {
        WebSettings settings = getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        CookieManager.getInstance().setAcceptCookie(false);
        // Disable save form data
        settings.setSaveFormData(false);
    }

    public Bitmap captureThumbnail(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }

    public void loadUrl(String url, boolean addToHistory) {
        if (url == null || url.isEmpty()) return;
        if (!url.startsWith("http://") && !url.startsWith("https://") &&
                !url.startsWith("surf://") && !url.startsWith("about:") &&
                !url.startsWith("file://") && !url.startsWith("data:")) {
            url = "https://" + url;
        }
        loadUrl(url);
    }

    public void clearAllData() {
        clearHistory();
        clearCache(true);
        clearFormData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }

    public void injectAdBlockCss(String css) {
        String script = "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = '" + css.replace("'", "\\'") + "';" +
                "document.head.appendChild(style);";
        evaluateJavascript(script, null);
    }

    public void injectReaderModeJs() {
        String script = "(function(){" +
                "var article = document.querySelector('article') || document.body;" +
                "var content = article.innerHTML;" +
                "document.body.innerHTML = '<div class=\"reader-content\">' + content + '</div>';" +
                "})();";
        evaluateJavascript(script, null);
    }

    public void extractPageText(android.webkit.ValueCallback<String> callback) {
        evaluateJavascript("document.body.innerText", callback);
    }

    public void extractLinks(android.webkit.ValueCallback<String> callback) {
        evaluateJavascript("JSON.stringify(Array.from(document.querySelectorAll('a[href]')).map(function(a){return{href:a.href,text:a.innerText.trim()}}))", callback);
    }

    public void extractVideoUrls(android.webkit.ValueCallback<String> callback) {
        evaluateJavascript(
            "JSON.stringify(Array.from(document.querySelectorAll('video,source')).map(function(v){return v.src||v.currentSrc}).filter(Boolean))",
            callback
        );
    }

    public void extractIframes(android.webkit.ValueCallback<String> callback) {
        evaluateJavascript(
            "JSON.stringify(Array.from(document.querySelectorAll('iframe[src]')).map(function(f){return{src:f.src,title:f.title||''}}))",
            callback
        );
    }
}
