package com.surffountain.browser.security;

import android.content.Context;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;

public class BiometricHelper {

    public interface AuthCallback {
        void onSuccess();
        void onError(String errorMessage);
        void onFailed();
    }

    private final FragmentActivity activity;

    public BiometricHelper(Context context) {
        this.activity = (FragmentActivity) context;
    }

    public boolean isBiometricAvailable() {
        BiometricManager manager = BiometricManager.from(activity);
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void authenticate(String title, String subtitle, AuthCallback callback) {
        if (!isBiometricAvailable()) {
            callback.onSuccess(); // Fall back to no auth
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(activity);

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                            callback.onError("Authentication cancelled");
                        } else {
                            callback.onError(errString.toString());
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onFailed();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
