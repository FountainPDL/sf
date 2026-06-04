package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.surffountain.browser.R;
import com.surffountain.browser.adapters.DownloadAdapter;
import com.surffountain.browser.databinding.ActivityDownloadsBinding;
import com.surffountain.browser.viewmodels.DownloadViewModel;

public class DownloadsActivity extends AppCompatActivity {

    private ActivityDownloadsBinding binding;
    private DownloadViewModel viewModel;
    private DownloadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.downloads);
        }

        viewModel = new ViewModelProvider(this).get(DownloadViewModel.class);
        setupTabs();
        setupRecyclerView();
        setupObservers();
    }

    private void setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Active"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Images"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Videos"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Documents"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: viewModel.loadAll(); break;
                    case 1: viewModel.loadActive(); break;
                    case 2: viewModel.loadByCategory("IMAGE"); break;
                    case 3: viewModel.loadByCategory("VIDEO"); break;
                    case 4: viewModel.loadByCategory("DOCUMENT"); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new DownloadAdapter(
            item -> viewModel.pauseOrResume(item),
            item -> viewModel.cancel(item),
            item -> viewModel.retry(item),
            item -> viewModel.openFile(item, this)
        );
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getDownloads().observe(this, downloads -> {
            adapter.submitList(downloads);
            binding.emptyView.setVisibility(downloads.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
