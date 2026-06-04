package com.surffountain.browser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.surffountain.browser.R;
import com.surffountain.browser.adapters.HistoryAdapter;
import com.surffountain.browser.databinding.ActivityHistoryBinding;
import com.surffountain.browser.viewmodels.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.history);
        }

        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        setupRecyclerView();
        setupSearch();
        setupObservers();
        setupClearButton();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(item -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(item.url));
            startActivity(intent);
            finish();
        }, item -> {
            viewModel.delete(item);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupObservers() {
        viewModel.getHistory().observe(this, history -> {
            adapter.submitList(history);
            binding.emptyView.setVisibility(history.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupClearButton() {
        binding.btnClearAll.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Clear History")
                    .setMessage("This will delete all browsing history. Continue?")
                    .setPositiveButton("Clear", (d, w) -> viewModel.clearAll())
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
