package com.surffountain.browser.webview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.settings.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public class SurfWebChromeClient extends WebChromeClient {

    public interface ChromeListener {
        void onProgressChanged(int progress);
        void onTitleChanged(String title);
        void onFaviconChanged(Bitmap favicon);
        void onEnterFullscreen(View customView);
        void onExitFullscreen();
        void onNewWindowRequested(WebView newWebView);
        void onConsoleMessage(String message, int lineNumber, String sourceId, ConsoleMessage.MessageLevel level);
        void onFileChooserRequest(ValueCallback<Uri[]> filePathCallback, FileChooserParams params);
        void onGeolocationPermissionRequest(String origin, GeolocationPermissions.Callback callback);
    }

    private final Activity activity;
    private ChromeListener chromeListener;
    private View fullscreenView;
    private CustomViewCallback fullscreenCallback;
    private final List<ConsoleMessage> consoleMessages = new ArrayList<>();

    public SurfWebChromeClient(Activity activity) {
        this.activity = activity;
    }

    public void setChromeListener(ChromeListener listener) {
        this.chromeListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (chromeListener != null) chromeListener.onProgressChanged(newProgress);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (chromeListener != null && title != null) chromeListener.onTitleChanged(title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (chromeListener != null && icon != null) chromeListener.onFaviconChanged(icon);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
        WebView newWebView = new WebView(activity);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();
        if (chromeListener != null) chromeListener.onNewWindowRequested(newWebView);
        return true;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (fullscreenView != null) {
            callback.onCustomViewHidden();
            return;
        }
        fullscreenView = view;
        fullscreenCallback = callback;
        if (chromeListener != null) chromeListener.onEnterFullscreen(view);
    }

    @Override
    public void onHideCustomView() {
        if (chromeListener != null) chromeListener.onExitFullscreen();
        fullscreenView = null;
        fullscreenCallback = null;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(Uri.parse(url).getHost())
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> result.confirm())
                .setOnCancelListener(d -> result.cancel())
                .show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(Uri.parse(url).getHost())
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> result.confirm())
                .setNegativeButton("Cancel", (d, w) -> result.cancel())
                .setOnCancelListener(d -> result.cancel())
                .show();
        return true;
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        String[] resources = request.getResources();
        List<String> androidPerms = new ArrayList<>();
        for (String resource : resources) {
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) {
                androidPerms.add(Manifest.permission.CAMERA);
            } else if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) {
                androidPerms.add(Manifest.permission.RECORD_AUDIO);
            }
        }
        SettingsManager sm = SurfFountainApp.getInstance().getSettingsManager();
        if (sm.isCameraEnabled() || sm.isMicEnabled()) {
            request.grant(resources);
        } else {
            request.deny();
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        SettingsManager sm = SurfFountainApp.getInstance().getSettingsManager();
        if (sm.isLocationEnabled()) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                callback.invoke(origin, true, false);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                callback.invoke(origin, false, false);
            }
        } else {
            callback.invoke(origin, false, false);
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        consoleMessages.add(consoleMessage);
        if (consoleMessages.size() > 500) consoleMessages.remove(0);
        if (chromeListener != null) {
            chromeListener.onConsoleMessage(
                    consoleMessage.message(),
                    consoleMessage.lineNumber(),
                    consoleMessage.sourceId(),
                    consoleMessage.messageLevel()
            );
        }
        return true;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                      FileChooserParams fileChooserParams) {
        if (chromeListener != null) {
            chromeListener.onFileChooserRequest(filePathCallback, fileChooserParams);
            return true;
        }
        return false;
    }

    public List<ConsoleMessage> getConsoleMessages() {
        return consoleMessages;
    }

    public View getFullscreenView() {
        return fullscreenView;
    }

    public void exitFullscreen() {
        if (fullscreenCallback != null) {
            fullscreenCallback.onCustomViewHidden();
        }
    }
}
