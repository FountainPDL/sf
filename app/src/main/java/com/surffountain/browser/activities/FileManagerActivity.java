package com.surffountain.browser.activities;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.surffountain.browser.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private File currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.rv_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        loadDirectory(currentDirectory);
    }

    private void loadDirectory(File dir) {
        if (dir == null || !dir.exists()) return;
        setTitle(dir.getName());
        File[] files = dir.listFiles();
        List<File> fileList = files != null ? Arrays.asList(files) : new ArrayList<>();
        // Adapter would be set here in a full implementation
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (currentDirectory != null && currentDirectory.getParentFile() != null) {
            currentDirectory = currentDirectory.getParentFile();
            loadDirectory(currentDirectory);
            return true;
        }
        finish();
        return true;
    }
}
