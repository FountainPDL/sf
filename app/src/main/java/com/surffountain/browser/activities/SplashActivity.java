package com.surffountain.browser.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.security.BiometricHelper;
import com.surffountain.browser.settings.SettingsManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

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
            launchMain();
        }
    }

    private void launchMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
