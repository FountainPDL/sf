package com.surffountain.browser.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.surffountain.browser.models.PrivacyStats;
import com.surffountain.browser.utils.ThemeUtils;

public class SettingsManager {

    private static final String PREFS_NAME = "surf_fountain_prefs";
    private static SettingsManager instance;
    private final SharedPreferences prefs;
    private final PrivacyStats sessionStats;

    // Keys
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_SEARCH_ENGINE = "search_engine";
    private static final String KEY_JAVASCRIPT = "javascript_enabled";
    private static final String KEY_COOKIES = "cookies_enabled";
    private static final String KEY_THIRD_PARTY_COOKIES = "third_party_cookies";
    private static final String KEY_LOCATION = "location_enabled";
    private static final String KEY_CAMERA = "camera_enabled";
    private static final String KEY_MIC = "mic_enabled";
    private static final String KEY_AD_BLOCK = "ad_block_enabled";
    private static final String KEY_TRACKER_BLOCK = "tracker_block_enabled";
    private static final String KEY_HTTPS_UPGRADE = "https_upgrade_enabled";
    private static final String KEY_COOKIE_BLOCK = "cookie_block_enabled";
    private static final String KEY_FINGERPRINT = "fingerprint_protection";
    private static final String KEY_SCRIPT_BLOCK = "script_block_enabled";
    private static final String KEY_URL_CLEANING = "url_cleaning_enabled";
    private static final String KEY_AMP_REDIRECT = "amp_redirect_enabled";
    private static final String KEY_SOCIAL_TRACKER = "social_tracker_block";
    private static final String KEY_SAVE_PASSWORDS = "save_passwords";
    private static final String KEY_AUTOFILL = "autofill_enabled";
    private static final String KEY_DO_NOT_TRACK = "do_not_track";
    private static final String KEY_CLEAR_ON_EXIT = "clear_on_exit";
    private static final String KEY_READER_FONT_SIZE = "reader_font_size";
    private static final String KEY_READER_THEME = "reader_theme";
    private static final String KEY_DOWNLOAD_PATH = "download_path";
    private static final String KEY_HOME_PAGE = "home_page";
    private static final String KEY_NEW_TAB_PAGE = "new_tab_page";
    private static final String KEY_CUSTOM_USER_AGENT = "custom_user_agent";
    private static final String KEY_TAB_LAYOUT = "tab_layout";
    private static final String KEY_BIOMETRIC_LOCK = "biometric_lock";
    private static final String KEY_BIOMETRIC_PRIVATE = "biometric_private_tabs";
    private static final String KEY_TOTAL_ADS_BLOCKED = "total_ads_blocked";
    private static final String KEY_TOTAL_TRACKERS_BLOCKED = "total_trackers_blocked";
    private static final String KEY_AI_PROVIDER = "ai_provider";
    private static final String KEY_AI_API_KEY = "ai_api_key";
    private static final String KEY_AI_MODEL = "ai_model";
    private static final String KEY_VPN_ENABLED = "vpn_enabled";
    private static final String KEY_VPN_REGION = "vpn_region";
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_SHOW_SPEED_DIAL = "show_speed_dial";
    private static final String KEY_SHOW_NEWS = "show_news";

    private SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sessionStats = new PrivacyStats();
        // Restore cumulative stats
        sessionStats.setAdsBlocked(prefs.getInt(KEY_TOTAL_ADS_BLOCKED, 0));
        sessionStats.setTrackersBlocked(prefs.getInt(KEY_TOTAL_TRACKERS_BLOCKED, 0));
    }

    public static SettingsManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsManager.class) {
                if (instance == null) {
                    instance = new SettingsManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // Theme
    public int getThemeMode() { return prefs.getInt(KEY_THEME, ThemeUtils.THEME_SYSTEM); }
    public void setThemeMode(int mode) { prefs.edit().putInt(KEY_THEME, mode).apply(); }

    // Search Engine
    public String getSearchEngine() { return prefs.getString(KEY_SEARCH_ENGINE, "brave"); }
    public void setSearchEngine(String engine) { prefs.edit().putString(KEY_SEARCH_ENGINE, engine).apply(); }

    // Privacy & Security
    public boolean isJavaScriptEnabled() { return prefs.getBoolean(KEY_JAVASCRIPT, true); }
    public void setJavaScriptEnabled(boolean v) { prefs.edit().putBoolean(KEY_JAVASCRIPT, v).apply(); }

    public boolean isCookiesEnabled() { return prefs.getBoolean(KEY_COOKIES, true); }
    public void setCookiesEnabled(boolean v) { prefs.edit().putBoolean(KEY_COOKIES, v).apply(); }

    public boolean isThirdPartyCookiesEnabled() { return prefs.getBoolean(KEY_THIRD_PARTY_COOKIES, false); }
    public void setThirdPartyCookiesEnabled(boolean v) { prefs.edit().putBoolean(KEY_THIRD_PARTY_COOKIES, v).apply(); }

    public boolean isLocationEnabled() { return prefs.getBoolean(KEY_LOCATION, false); }
    public void setLocationEnabled(boolean v) { prefs.edit().putBoolean(KEY_LOCATION, v).apply(); }

    public boolean isCameraEnabled() { return prefs.getBoolean(KEY_CAMERA, false); }
    public void setCameraEnabled(boolean v) { prefs.edit().putBoolean(KEY_CAMERA, v).apply(); }

    public boolean isMicEnabled() { return prefs.getBoolean(KEY_MIC, false); }
    public void setMicEnabled(boolean v) { prefs.edit().putBoolean(KEY_MIC, v).apply(); }

    public boolean isAdBlockEnabled() { return prefs.getBoolean(KEY_AD_BLOCK, true); }
    public void setAdBlockEnabled(boolean v) { prefs.edit().putBoolean(KEY_AD_BLOCK, v).apply(); }

    public boolean isTrackerBlockEnabled() { return prefs.getBoolean(KEY_TRACKER_BLOCK, true); }
    public void setTrackerBlockEnabled(boolean v) { prefs.edit().putBoolean(KEY_TRACKER_BLOCK, v).apply(); }

    public boolean isHttpsUpgradeEnabled() { return prefs.getBoolean(KEY_HTTPS_UPGRADE, true); }
    public void setHttpsUpgradeEnabled(boolean v) { prefs.edit().putBoolean(KEY_HTTPS_UPGRADE, v).apply(); }

    public boolean isCookieBlockEnabled() { return prefs.getBoolean(KEY_COOKIE_BLOCK, false); }
    public void setCookieBlockEnabled(boolean v) { prefs.edit().putBoolean(KEY_COOKIE_BLOCK, v).apply(); }

    public boolean isFingerprintProtectionEnabled() { return prefs.getBoolean(KEY_FINGERPRINT, true); }
    public void setFingerprintProtectionEnabled(boolean v) { prefs.edit().putBoolean(KEY_FINGERPRINT, v).apply(); }

    public boolean isScriptBlockEnabled() { return prefs.getBoolean(KEY_SCRIPT_BLOCK, false); }
    public void setScriptBlockEnabled(boolean v) { prefs.edit().putBoolean(KEY_SCRIPT_BLOCK, v).apply(); }

    public boolean isUrlCleaningEnabled() { return prefs.getBoolean(KEY_URL_CLEANING, true); }
    public void setUrlCleaningEnabled(boolean v) { prefs.edit().putBoolean(KEY_URL_CLEANING, v).apply(); }

    public boolean isAmpRedirectEnabled() { return prefs.getBoolean(KEY_AMP_REDIRECT, true); }
    public void setAmpRedirectEnabled(boolean v) { prefs.edit().putBoolean(KEY_AMP_REDIRECT, v).apply(); }

    public boolean isSocialTrackerBlockEnabled() { return prefs.getBoolean(KEY_SOCIAL_TRACKER, true); }
    public void setSocialTrackerBlockEnabled(boolean v) { prefs.edit().putBoolean(KEY_SOCIAL_TRACKER, v).apply(); }

    public boolean isSavePasswordsEnabled() { return prefs.getBoolean(KEY_SAVE_PASSWORDS, true); }
    public void setSavePasswordsEnabled(boolean v) { prefs.edit().putBoolean(KEY_SAVE_PASSWORDS, v).apply(); }

    public boolean isAutofillEnabled() { return prefs.getBoolean(KEY_AUTOFILL, true); }
    public void setAutofillEnabled(boolean v) { prefs.edit().putBoolean(KEY_AUTOFILL, v).apply(); }

    public boolean isDoNotTrackEnabled() { return prefs.getBoolean(KEY_DO_NOT_TRACK, true); }
    public void setDoNotTrackEnabled(boolean v) { prefs.edit().putBoolean(KEY_DO_NOT_TRACK, v).apply(); }

    public boolean isClearOnExitEnabled() { return prefs.getBoolean(KEY_CLEAR_ON_EXIT, false); }
    public void setClearOnExitEnabled(boolean v) { prefs.edit().putBoolean(KEY_CLEAR_ON_EXIT, v).apply(); }

    // Downloads
    public String getDownloadPath() { return prefs.getString(KEY_DOWNLOAD_PATH, null); }
    public void setDownloadPath(String path) { prefs.edit().putString(KEY_DOWNLOAD_PATH, path).apply(); }

    // Navigation
    public String getHomePage() { return prefs.getString(KEY_HOME_PAGE, "surf://home"); }
    public void setHomePage(String url) { prefs.edit().putString(KEY_HOME_PAGE, url).apply(); }
    public String getNewTabPage() { return prefs.getString(KEY_NEW_TAB_PAGE, "surf://newtab"); }
    public void setNewTabPage(String url) { prefs.edit().putString(KEY_NEW_TAB_PAGE, url).apply(); }

    // Tab layout
    public String getTabLayout() { return prefs.getString(KEY_TAB_LAYOUT, "grid"); }
    public void setTabLayout(String layout) { prefs.edit().putString(KEY_TAB_LAYOUT, layout).apply(); }

    // Biometric
    public boolean isBiometricLockEnabled() { return prefs.getBoolean(KEY_BIOMETRIC_LOCK, false); }
    public void setBiometricLockEnabled(boolean v) { prefs.edit().putBoolean(KEY_BIOMETRIC_LOCK, v).apply(); }
    public boolean isBiometricForPrivateTabs() { return prefs.getBoolean(KEY_BIOMETRIC_PRIVATE, false); }
    public void setBiometricForPrivateTabs(boolean v) { prefs.edit().putBoolean(KEY_BIOMETRIC_PRIVATE, v).apply(); }

    // AI
    public String getAiProvider() { return prefs.getString(KEY_AI_PROVIDER, "gemini"); }
    public void setAiProvider(String provider) { prefs.edit().putString(KEY_AI_PROVIDER, provider).apply(); }
    public String getAiApiKey() { return prefs.getString(KEY_AI_API_KEY, ""); }
    public void setAiApiKey(String key) { prefs.edit().putString(KEY_AI_API_KEY, key).apply(); }
    public String getAiModel() { return prefs.getString(KEY_AI_MODEL, "gemini-pro"); }
    public void setAiModel(String model) { prefs.edit().putString(KEY_AI_MODEL, model).apply(); }

    // Appearance
    public int getAccentColor() { return prefs.getInt(KEY_ACCENT_COLOR, 0xFF0096C7); }
    public void setAccentColor(int color) { prefs.edit().putInt(KEY_ACCENT_COLOR, color).apply(); }
    public String getFontSize() { return prefs.getString(KEY_FONT_SIZE, "medium"); }
    public void setFontSize(String size) { prefs.edit().putString(KEY_FONT_SIZE, size).apply(); }
    public boolean isShowSpeedDial() { return prefs.getBoolean(KEY_SHOW_SPEED_DIAL, true); }
    public void setShowSpeedDial(boolean v) { prefs.edit().putBoolean(KEY_SHOW_SPEED_DIAL, v).apply(); }
    public boolean isShowNews() { return prefs.getBoolean(KEY_SHOW_NEWS, true); }
    public void setShowNews(boolean v) { prefs.edit().putBoolean(KEY_SHOW_NEWS, v).apply(); }

    // Custom user agent
    public String getCustomUserAgent() { return prefs.getString(KEY_CUSTOM_USER_AGENT, ""); }
    public void setCustomUserAgent(String ua) { prefs.edit().putString(KEY_CUSTOM_USER_AGENT, ua).apply(); }

    // Reader mode
    public int getReaderFontSize() { return prefs.getInt(KEY_READER_FONT_SIZE, 18); }
    public void setReaderFontSize(int size) { prefs.edit().putInt(KEY_READER_FONT_SIZE, size).apply(); }
    public String getReaderTheme() { return prefs.getString(KEY_READER_THEME, "light"); }
    public void setReaderTheme(String theme) { prefs.edit().putString(KEY_READER_THEME, theme).apply(); }

    // Stats
    public PrivacyStats getPrivacyStats() { return sessionStats; }
    public void saveStats() {
        prefs.edit()
            .putInt(KEY_TOTAL_ADS_BLOCKED, sessionStats.getAdsBlocked())
            .putInt(KEY_TOTAL_TRACKERS_BLOCKED, sessionStats.getTrackersBlocked())
            .apply();
    }

    public void clearAllData(android.content.Context ctx) {
        prefs.edit().clear().apply();
        android.webkit.WebStorage.getInstance().deleteAllData();
        android.webkit.CookieManager.getInstance().removeAllCookies(null);
    }
}
