package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.databinding.ActivitySettingsBinding;
import com.surffountain.browser.settings.SettingsManager;
import com.surffountain.browser.utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        String section = getIntent().getStringExtra("section");
        if (section == null) section = "general";

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_container, SettingsFragment.newInstance(section))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SettingsManager sm;

        public static SettingsFragment newInstance(String section) {
            SettingsFragment f = new SettingsFragment();
            Bundle args = new Bundle();
            args.putString("section", section);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            sm = SurfFountainApp.getInstance().getSettingsManager();
            String section = getArguments() != null ? getArguments().getString("section", "general") : "general";

            switch (section) {
                case "privacy": setPreferencesFromResource(R.xml.prefs_privacy, rootKey); bindPrivacy(); break;
                case "security": setPreferencesFromResource(R.xml.prefs_security, rootKey); break;
                case "appearance": setPreferencesFromResource(R.xml.prefs_appearance, rootKey); bindAppearance(); break;
                case "downloads": setPreferencesFromResource(R.xml.prefs_downloads, rootKey); break;
                case "ai": setPreferencesFromResource(R.xml.prefs_ai, rootKey); break;
                case "accessibility": setPreferencesFromResource(R.xml.prefs_accessibility, rootKey); break;
                case "developer": setPreferencesFromResource(R.xml.prefs_developer, rootKey); break;
                default: setPreferencesFromResource(R.xml.prefs_general, rootKey); bindGeneral(); break;
            }
        }

        private void bindGeneral() {
            ListPreference searchEngine = findPreference("search_engine");
            if (searchEngine != null) {
                searchEngine.setValue(sm.getSearchEngine());
                searchEngine.setOnPreferenceChangeListener((p, v) -> {
                    sm.setSearchEngine((String) v);
                    return true;
                });
            }
            SwitchPreferenceCompat javascript = findPreference("javascript");
            if (javascript != null) {
                javascript.setChecked(sm.isJavaScriptEnabled());
                javascript.setOnPreferenceChangeListener((p, v) -> {
                    sm.setJavaScriptEnabled((Boolean) v);
                    return true;
                });
            }
        }

        private void bindPrivacy() {
            SwitchPreferenceCompat adBlock = findPreference("ad_block");
            if (adBlock != null) {
                adBlock.setChecked(sm.isAdBlockEnabled());
                adBlock.setOnPreferenceChangeListener((p, v) -> {
                    sm.setAdBlockEnabled((Boolean) v);
                    return true;
                });
            }
            SwitchPreferenceCompat tracker = findPreference("tracker_block");
            if (tracker != null) {
                tracker.setChecked(sm.isTrackerBlockEnabled());
                tracker.setOnPreferenceChangeListener((p, v) -> {
                    sm.setTrackerBlockEnabled((Boolean) v);
                    return true;
                });
            }
        }

        private void bindAppearance() {
            ListPreference theme = findPreference("theme_mode");
            if (theme != null) {
                theme.setValue(String.valueOf(sm.getThemeMode()));
                theme.setOnPreferenceChangeListener((p, v) -> {
                    int mode = Integer.parseInt((String) v);
                    sm.setThemeMode(mode);
                    ThemeUtils.applyTheme(mode);
                    return true;
                });
            }
        }
    }
}
