package com.surffountain.browser.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.privacy.AdBlocker;
import com.surffountain.browser.privacy.SurfShield;

import java.io.ByteArrayInputStream;

public class SurfWebViewClient extends WebViewClient {

    private final Context context;
    private final SurfShield surfShield;
    private final AdBlocker adBlocker;
    private SurfWebView.PageListener pageListener;

    public SurfWebViewClient(Context context) {
        this.context = context;
        this.surfShield = SurfShield.getInstance(context);
        this.adBlocker = SurfFountainApp.getInstance().getAdBlocker();
    }

    public void setPageListener(SurfWebView.PageListener listener) {
        this.pageListener = listener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (pageListener != null) {
            pageListener.onPageStarted(url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // Inject cosmetic ad blocking CSS
        if (adBlocker != null && adBlocker.isEnabled()) {
            String css = adBlocker.getCosmeticFilterCss(url);
            if (!css.isEmpty() && view instanceof SurfWebView) {
                ((SurfWebView) view).injectAdBlockCss(css);
            }
        }
        if (pageListener != null) {
            pageListener.onPageFinished(url);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();

        // HTTPS upgrade
        if (surfShield.shouldUpgradeToHttps(url)) {
            String httpsUrl = url.replace("http://", "https://");
            view.post(() -> view.loadUrl(httpsUrl));
            return new WebResourceResponse("text/plain", "utf-8",
                    new ByteArrayInputStream("".getBytes()));
        }

        // Ad / tracker blocking
        if (adBlocker != null && adBlocker.isEnabled()) {
            if (adBlocker.shouldBlock(url, request.getRequestHeaders())) {
                SurfFountainApp.getInstance().getSettingsManager()
                        .getPrivacyStats().incrementAdsBlocked();
                return new WebResourceResponse("text/plain", "utf-8",
                        new ByteArrayInputStream("".getBytes()));
            }
        }

        // AMP redirect blocking
        if (surfShield.isAmpUrl(url)) {
            String canonical = surfShield.getAmpCanonicalUrl(url);
            if (canonical != null) {
                view.post(() -> view.loadUrl(canonical));
                return new WebResourceResponse("text/plain", "utf-8",
                        new ByteArrayInputStream("".getBytes()));
            }
        }

        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();

        // Handle special schemes
        if (url.startsWith("surf://")) {
            handleSurfScheme(view, url);
            return true;
        }

        // Handle intent schemes, tel, mailto, etc.
        Uri uri = request.getUrl();
        String scheme = uri.getScheme();
        if (scheme != null && !scheme.equals("http") && !scheme.equals("https") &&
                !scheme.equals("data") && !scheme.equals("blob") && !scheme.equals("file")) {
            try {
                android.content.Intent intent = android.content.Intent.parseUri(url,
                        android.content.Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
            } catch (Exception e) {
                // Cannot handle
            }
            return true;
        }

        return false;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // Block SSL errors by default for security
        handler.cancel();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        // Load custom error page
        String errorHtml = buildErrorPage(errorCode, description, failingUrl);
        view.loadDataWithBaseURL(null, errorHtml, "text/html", "utf-8", null);
    }

    private void handleSurfScheme(WebView view, String url) {
        if (url.equals("surf://newtab") || url.equals("surf://home")) {
            view.loadUrl("about:blank");
        } else if (url.equals("surf://settings")) {
            // Launch settings
        }
    }

    private String buildErrorPage(int code, String description, String url) {
        return "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<style>body{font-family:sans-serif;text-align:center;padding:50px;background:#1a1a2e;color:#eee;}" +
                "h1{font-size:48px;color:#0096c7;}p{font-size:18px;opacity:0.7;}" +
                "button{margin-top:20px;padding:12px 24px;background:#0096c7;color:#fff;border:none;" +
                "border-radius:8px;font-size:16px;cursor:pointer;}" +
                "</style></head><body>" +
                "<h1>:(</h1>" +
                "<h2>Page Not Available</h2>" +
                "<p>Error " + code + ": " + description + "</p>" +
                "<p>" + url + "</p>" +
                "<button onclick='history.back()'>Go Back</button>" +
                "<button onclick='location.reload()' style='margin-left:10px;background:#444;'>Retry</button>" +
                "</body></html>";
    }
}
