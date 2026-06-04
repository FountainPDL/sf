package com.surffountain.browser.viewmodels;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.PasswordEntity;
import com.surffountain.browser.security.PasswordManager;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AppDatabase db = SurfFountainApp.getInstance().getDatabase();
    private LiveData<List<PasswordEntity>> passwords;
    private final PasswordManager passwordManager;

    public PasswordViewModel() {
        passwords = db.passwordDao().getAllPasswords();
        passwordManager = new PasswordManager(SurfFountainApp.getAppContext());
    }

    public LiveData<List<PasswordEntity>> getPasswords() { return passwords; }

    public void search(String query) {
        passwords = db.passwordDao().search(query);
    }

    public void delete(PasswordEntity item) {
        executor.execute(() -> db.passwordDao().delete(item));
    }

    public void copyPassword(PasswordEntity item, Context context) {
        executor.execute(() -> {
            String decrypted = passwordManager.decrypt(item.encryptedPassword, item.encryptionIv);
            if (decrypted != null) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("password", decrypted));
                    Toast.makeText(context, "Password copied", Toast.LENGTH_SHORT).show();
                });
                db.passwordDao().recordUse(item.id, System.currentTimeMillis());
            }
        });
    }

    public String generatePassword(int length, boolean upper, boolean lower, boolean digits, boolean symbols) {
        String chars = "";
        if (upper) chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (lower) chars += "abcdefghijklmnopqrstuvwxyz";
        if (digits) chars += "0123456789";
        if (symbols) chars += "!@#$%^&*()-_=+[]{}|;:,.<>?";
        if (chars.isEmpty()) chars = "abcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public int scorePassword(String password) {
        int score = 0;
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 20;
        if (password.matches(".*[A-Z].*")) score += 15;
        if (password.matches(".*[a-z].*")) score += 15;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()].*")) score += 15;
        return Math.min(score, 100);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
