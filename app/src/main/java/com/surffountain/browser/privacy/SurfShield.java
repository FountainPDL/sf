package com.surffountain.browser.privacy;

import android.content.Context;
import android.webkit.CookieManager;

import com.surffountain.browser.settings.SettingsManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SurfShield {

    private static SurfShield instance;
    private final Context context;
    private final SettingsManager settingsManager;

    // Social tracker domains
    private static final Set<String> SOCIAL_TRACKERS = new HashSet<>(Arrays.asList(
        "facebook.com", "twitter.com", "linkedin.com", "pinterest.com",
        "instagram.com", "tiktok.com", "snapchat.com", "reddit.com"
    ));

    // Fingerprinting scripts
    private static final String[] FINGERPRINT_SCRIPTS = {
        "fingerprintjs", "fp.js", "fingerprintsurvey", "browser-fingerprint",
        "deviceprint", "fp2.js"
    };

    // HTTPS upgradeable domains (popular sites that support HTTPS)
    private static final Set<String> HTTPS_UPGRADEABLE = new HashSet<>(Arrays.asList(
        "example.com", "wikipedia.org", "bbc.com", "cnn.com"
    ));

    // AMP URL patterns
    private static final Pattern AMP_PATTERN = Pattern.compile(
        "(/amp/|[?&]amp=1|amp\\.\\w+\\.com|[?&]source=amp)", Pattern.CASE_INSENSITIVE
    );

    // URL tracking parameters to clean
    private static final String[] TRACKING_PARAMS = {
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
        "fbclid", "gclid", "msclkid", "mc_eid", "ref", "referrer",
        "_ga", "_gl", "yclid", "twclid"
    };

    private boolean adBlockEnabled = true;
    private boolean trackerBlockEnabled = true;
    private boolean httpsUpgradeEnabled = true;
    private boolean cookieBlockEnabled = false;
    private boolean fingerprintProtectionEnabled = true;
    private boolean scriptBlockEnabled = false;
    private boolean urlCleaningEnabled = true;
    private boolean ampRedirectEnabled = true;
    private boolean socialTrackerBlockEnabled = true;

    private SurfShield(Context context) {
        this.context = context.getApplicationContext();
        this.settingsManager = SettingsManager.getInstance(context);
        loadSettings();
    }

    public static SurfShield getInstance(Context context) {
        if (instance == null) {
            synchronized (SurfShield.class) {
                if (instance == null) {
                    instance = new SurfShield(context);
                }
            }
        }
        return instance;
    }

    private void loadSettings() {
        adBlockEnabled = settingsManager.isAdBlockEnabled();
        trackerBlockEnabled = settingsManager.isTrackerBlockEnabled();
        httpsUpgradeEnabled = settingsManager.isHttpsUpgradeEnabled();
        cookieBlockEnabled = settingsManager.isCookieBlockEnabled();
        fingerprintProtectionEnabled = settingsManager.isFingerprintProtectionEnabled();
        scriptBlockEnabled = settingsManager.isScriptBlockEnabled();
        urlCleaningEnabled = settingsManager.isUrlCleaningEnabled();
        ampRedirectEnabled = settingsManager.isAmpRedirectEnabled();
        socialTrackerBlockEnabled = settingsManager.isSocialTrackerBlockEnabled();
    }

    public boolean shouldUpgradeToHttps(String url) {
        if (!httpsUpgradeEnabled) return false;
        return url != null && url.startsWith("http://");
    }

    public boolean isSocialTracker(String url) {
        if (!socialTrackerBlockEnabled || url == null) return false;
        for (String domain : SOCIAL_TRACKERS) {
            if (url.contains(domain + "/tr") || url.contains(domain + "/pixel") ||
                url.contains("connect." + domain)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFingerprintScript(String url) {
        if (!fingerprintProtectionEnabled || url == null) return false;
        String lower = url.toLowerCase();
        for (String script : FINGERPRINT_SCRIPTS) {
            if (lower.contains(script)) return true;
        }
        return false;
    }

    public boolean isAmpUrl(String url) {
        if (!ampRedirectEnabled || url == null) return false;
        return AMP_PATTERN.matcher(url).find();
    }

    public String getAmpCanonicalUrl(String ampUrl) {
        if (ampUrl == null) return null;
        // Strip common AMP patterns
        String canonical = ampUrl;
        canonical = canonical.replaceAll("[?&]amp=1", "");
        canonical = canonical.replaceAll("/amp/", "/");
        canonical = canonical.replaceAll("amp\\.", "");
        return canonical.equals(ampUrl) ? null : canonical;
    }

    public String cleanUrl(String url) {
        if (!urlCleaningEnabled || url == null) return url;
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            android.net.Uri.Builder builder = uri.buildUpon();
            builder.clearQuery();
            String query = uri.getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String paramName = param.split("=")[0];
                    boolean isTracking = false;
                    for (String tp : TRACKING_PARAMS) {
                        if (paramName.equalsIgnoreCase(tp)) {
                            isTracking = true;
                            break;
                        }
                    }
                    if (!isTracking) builder.appendQueryParameter(paramName, uri.getQueryParameter(paramName));
                }
            }
            return builder.build().toString();
        } catch (Exception e) {
            return url;
        }
    }

    public void blockThirdPartyCookies() {
        if (cookieBlockEnabled) {
            CookieManager.getInstance().removeAllCookies(null);
        }
    }

    // Shield status summary
    public boolean isAdBlockEnabled() { return adBlockEnabled; }
    public boolean isTrackerBlockEnabled() { return trackerBlockEnabled; }
    public boolean isHttpsUpgradeEnabled() { return httpsUpgradeEnabled; }
    public boolean isCookieBlockEnabled() { return cookieBlockEnabled; }
    public boolean isFingerprintProtectionEnabled() { return fingerprintProtectionEnabled; }
    public boolean isScriptBlockEnabled() { return scriptBlockEnabled; }
    public boolean isUrlCleaningEnabled() { return urlCleaningEnabled; }
    public boolean isAmpRedirectEnabled() { return ampRedirectEnabled; }
    public boolean isSocialTrackerBlockEnabled() { return socialTrackerBlockEnabled; }

    public void setAdBlockEnabled(boolean v) { adBlockEnabled = v; settingsManager.setAdBlockEnabled(v); }
    public void setTrackerBlockEnabled(boolean v) { trackerBlockEnabled = v; settingsManager.setTrackerBlockEnabled(v); }
    public void setHttpsUpgradeEnabled(boolean v) { httpsUpgradeEnabled = v; settingsManager.setHttpsUpgradeEnabled(v); }
    public void setCookieBlockEnabled(boolean v) { cookieBlockEnabled = v; settingsManager.setCookieBlockEnabled(v); }
    public void setFingerprintProtectionEnabled(boolean v) { fingerprintProtectionEnabled = v; settingsManager.setFingerprintProtectionEnabled(v); }
    public void setScriptBlockEnabled(boolean v) { scriptBlockEnabled = v; settingsManager.setScriptBlockEnabled(v); }
    public void setUrlCleaningEnabled(boolean v) { urlCleaningEnabled = v; settingsManager.setUrlCleaningEnabled(v); }
    public void setAmpRedirectEnabled(boolean v) { ampRedirectEnabled = v; settingsManager.setAmpRedirectEnabled(v); }
    public void setSocialTrackerBlockEnabled(boolean v) { socialTrackerBlockEnabled = v; settingsManager.setSocialTrackerBlockEnabled(v); }
}
