package com.surffountain.browser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.security.BiometricHelper;
import com.surffountain.browser.settings.SettingsManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsManager sm = SurfFountainApp.getInstance().getSettingsManager();

        if (sm.isBiometricLockEnabled()) {
            BiometricHelper biometric = new BiometricHelper(this);
            biometric.authenticate(
                "Unlock Surf Fountain",
                "Use biometric to unlock the browser",
                new BiometricHelper.AuthCallback() {
                    @Override public void onSuccess() { launchMain(); }
                    @Override public void onError(String error) { finish(); }
                    @Override public void onFailed() { finish(); }
                }
            );
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(this::launchMain, SPLASH_DELAY_MS);
        }
    }

    private void launchMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
