package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.databinding.ActivityPrivacyCenterBinding;
import com.surffountain.browser.models.PrivacyStats;
import com.surffountain.browser.privacy.SurfShield;
import com.surffountain.browser.settings.SettingsManager;

public class PrivacyCenterActivity extends AppCompatActivity {

    private ActivityPrivacyCenterBinding binding;
    private SettingsManager settingsManager;
    private SurfShield surfShield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Privacy Center");
        }

        settingsManager = SurfFountainApp.getInstance().getSettingsManager();
        surfShield = SurfShield.getInstance(this);

        updateStats();
        setupShieldToggles();
    }

    private void updateStats() {
        PrivacyStats stats = settingsManager.getPrivacyStats();
        binding.tvAdsBlocked.setText(String.valueOf(stats.getAdsBlocked()));
        binding.tvTrackersBlocked.setText(String.valueOf(stats.getTrackersBlocked()));
        binding.tvHttpsUpgrades.setText(String.valueOf(stats.getHttpsUpgrades()));
        binding.tvDataSaved.setText(stats.getFormattedDataSaved());
        binding.tvTotalBlocked.setText(String.valueOf(stats.getTotalBlocked()));
    }

    private void setupShieldToggles() {
        binding.switchAdBlock.setChecked(surfShield.isAdBlockEnabled());
        binding.switchTrackers.setChecked(surfShield.isTrackerBlockEnabled());
        binding.switchHttps.setChecked(surfShield.isHttpsUpgradeEnabled());
        binding.switchFingerprint.setChecked(surfShield.isFingerprintProtectionEnabled());
        binding.switchCookies.setChecked(surfShield.isCookieBlockEnabled());
        binding.switchAmp.setChecked(surfShield.isAmpRedirectEnabled());
        binding.switchUrlCleaning.setChecked(surfShield.isUrlCleaningEnabled());
        binding.switchSocialTrackers.setChecked(surfShield.isSocialTrackerBlockEnabled());

        binding.switchAdBlock.setOnCheckedChangeListener((b, v) -> surfShield.setAdBlockEnabled(v));
        binding.switchTrackers.setOnCheckedChangeListener((b, v) -> surfShield.setTrackerBlockEnabled(v));
        binding.switchHttps.setOnCheckedChangeListener((b, v) -> surfShield.setHttpsUpgradeEnabled(v));
        binding.switchFingerprint.setOnCheckedChangeListener((b, v) -> surfShield.setFingerprintProtectionEnabled(v));
        binding.switchCookies.setOnCheckedChangeListener((b, v) -> surfShield.setCookieBlockEnabled(v));
        binding.switchAmp.setOnCheckedChangeListener((b, v) -> surfShield.setAmpRedirectEnabled(v));
        binding.switchUrlCleaning.setOnCheckedChangeListener((b, v) -> surfShield.setUrlCleaningEnabled(v));
        binding.switchSocialTrackers.setOnCheckedChangeListener((b, v) -> surfShield.setSocialTrackerBlockEnabled(v));

        binding.btnClearData.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Clear Browsing Data")
                    .setMessage("Delete cookies, cache, history, and site data?")
                    .setPositiveButton("Clear", (d, w) -> {
                        settingsManager.clearAllData(this);
                        updateStats();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
