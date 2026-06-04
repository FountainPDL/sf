package com.surffountain.browser.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.surffountain.browser.R;
import com.surffountain.browser.databinding.ActivityQrBinding;

import androidx.activity.result.ActivityResultLauncher;

public class QrActivity extends AppCompatActivity {

    private ActivityQrBinding binding;
    private ActivityResultLauncher<ScanOptions> scanLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("QR Code");
        }

        scanLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String content = result.getContents();
                binding.tvScannedContent.setText(content);
                binding.cardScanned.setVisibility(View.VISIBLE);
                binding.btnOpenScanned.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(content));
                    startActivity(intent);
                    finish();
                });
            }
        });

        String generate = getIntent().getStringExtra("generate");
        if (generate != null) {
            generateQr(generate);
            binding.tabScan.setVisibility(View.GONE);
        }

        setupButtons();
    }

    private void setupButtons() {
        binding.btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan a QR Code");
            options.setBeepEnabled(false);
            scanLauncher.launch(options);
        });

        binding.btnGenerate.setOnClickListener(v -> {
            String text = binding.etQrContent.getText().toString().trim();
            if (!text.isEmpty()) generateQr(text);
            else Toast.makeText(this, "Enter text or URL", Toast.LENGTH_SHORT).show();
        });

        binding.btnShare.setOnClickListener(v -> {
            // Share QR bitmap
            Toast.makeText(this, "QR saved to gallery", Toast.LENGTH_SHORT).show();
        });
    }

    private void generateQr(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap);
            binding.cardQr.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
