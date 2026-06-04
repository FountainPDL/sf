package com.surffountain.browser.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.databinding.ActivityReaderModeBinding;
import com.surffountain.browser.settings.SettingsManager;
import com.surffountain.browser.utils.ReaderModeExtractor;

import java.util.Locale;

public class ReaderModeActivity extends AppCompatActivity {

    private ActivityReaderModeBinding binding;
    private SettingsManager settingsManager;
    private TextToSpeech tts;
    private boolean ttsPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReaderModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reader Mode");
        }

        settingsManager = SurfFountainApp.getInstance().getSettingsManager();

        String text = getIntent().getStringExtra("text");
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        binding.tvTitle.setText(title != null ? title : "");
        binding.tvUrl.setText(url != null ? url : "");

        if (text != null) {
            String cleaned = ReaderModeExtractor.cleanText(text);
            binding.tvContent.setText(cleaned);
        }

        applySettings();
        setupControls();
        initTts();
    }

    private void applySettings() {
        int fontSize = settingsManager.getReaderFontSize();
        binding.tvContent.setTextSize(fontSize);
        binding.seekFontSize.setProgress(fontSize - 12);

        String theme = settingsManager.getReaderTheme();
        applyTheme(theme);
    }

    private void setupControls() {
        binding.seekFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress + 12;
                binding.tvContent.setTextSize(size);
                if (fromUser) settingsManager.setReaderFontSize(size);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.btnLight.setOnClickListener(v -> applyTheme("light"));
        binding.btnSepia.setOnClickListener(v -> applyTheme("sepia"));
        binding.btnDark.setOnClickListener(v -> applyTheme("dark"));
        binding.btnAmoled.setOnClickListener(v -> applyTheme("amoled"));

        binding.btnTts.setOnClickListener(v -> {
            if (ttsPlaying) {
                tts.stop();
                ttsPlaying = false;
                binding.btnTts.setIconResource(R.drawable.ic_play);
            } else {
                String content = binding.tvContent.getText().toString();
                tts.speak(content, TextToSpeech.QUEUE_FLUSH, null, "reader");
                ttsPlaying = true;
                binding.btnTts.setIconResource(R.drawable.ic_pause);
            }
        });
    }

    private void applyTheme(String theme) {
        settingsManager.setReaderTheme(theme);
        switch (theme) {
            case "light":
                binding.scrollView.setBackgroundColor(0xFFFFFFFF);
                binding.tvContent.setTextColor(0xFF212121);
                binding.tvTitle.setTextColor(0xFF212121);
                break;
            case "sepia":
                binding.scrollView.setBackgroundColor(0xFFF5E6C8);
                binding.tvContent.setTextColor(0xFF3E2723);
                binding.tvTitle.setTextColor(0xFF3E2723);
                break;
            case "dark":
                binding.scrollView.setBackgroundColor(0xFF1E1E1E);
                binding.tvContent.setTextColor(0xFFE0E0E0);
                binding.tvTitle.setTextColor(0xFFFFFFFF);
                break;
            case "amoled":
                binding.scrollView.setBackgroundColor(0xFF000000);
                binding.tvContent.setTextColor(0xFFE0E0E0);
                binding.tvTitle.setTextColor(0xFFFFFFFF);
                break;
        }
    }

    private void initTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
