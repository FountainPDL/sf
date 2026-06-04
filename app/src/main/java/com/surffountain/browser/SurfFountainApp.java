package com.surffountain.browser;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Configuration;

import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.privacy.AdBlocker;
import com.surffountain.browser.settings.SettingsManager;
import com.surffountain.browser.utils.CrashHandler;
import com.surffountain.browser.utils.ThemeUtils;

public class SurfFountainApp extends Application implements Configuration.Provider {

    private static SurfFountainApp instance;
    private AppDatabase database;
    private SettingsManager settingsManager;
    private AdBlocker adBlocker;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Install custom crash handler
        CrashHandler.install(this);

        // Initialize settings first
        settingsManager = SettingsManager.getInstance(this);

        // Apply theme from settings
        applyTheme();

        // Enable WebView debugging in debug builds
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // Initialize database
        database = AppDatabase.getInstance(this);

        // Initialize AdBlocker in background
        initAdBlocker();
    }

    private void applyTheme() {
        int themeMode = settingsManager.getThemeMode();
        switch (themeMode) {
            case ThemeUtils.THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case ThemeUtils.THEME_DARK:
            case ThemeUtils.THEME_AMOLED:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void initAdBlocker() {
        new Thread(() -> {
            adBlocker = AdBlocker.getInstance(this);
            adBlocker.initialize();
        }, "AdBlockerInit").start();
    }

    public static SurfFountainApp getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public AdBlocker getAdBlocker() {
        return adBlocker;
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(BuildConfig.DEBUG ? android.util.Log.DEBUG : android.util.Log.ERROR)
                .build();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
