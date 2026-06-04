package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.surffountain.browser.R;
import com.surffountain.browser.adapters.ChatAdapter;
import com.surffountain.browser.databinding.ActivityAiAssistantBinding;
import com.surffountain.browser.models.ChatMessage;
import com.surffountain.browser.viewmodels.AiViewModel;

public class AiAssistantActivity extends AppCompatActivity {

    private ActivityAiAssistantBinding binding;
    private AiViewModel viewModel;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Surf AI");
        }

        viewModel = new ViewModelProvider(this).get(AiViewModel.class);

        String pageContent = getIntent().getStringExtra("page_content");
        String pageUrl = getIntent().getStringExtra("page_url");
        if (pageContent != null) viewModel.setPageContext(pageContent, pageUrl);

        setupRecyclerView();
        setupInput();
        setupChips();
        setupObservers();
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupInput() {
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                binding.inputMessage.setText("");
            }
        });
    }

    private void setupChips() {
        binding.chipSummarize.setOnClickListener(v -> viewModel.sendMessage("Summarize this page"));
        binding.chipExplain.setOnClickListener(v -> viewModel.sendMessage("Explain the main topic of this page"));
        binding.chipTranslate.setOnClickListener(v -> viewModel.sendMessage("Translate this page to English"));
        binding.chipRewrite.setOnClickListener(v -> viewModel.sendMessage("Rewrite this in simpler language"));
    }

    private void setupObservers() {
        viewModel.getMessages().observe(this, messages -> {
            adapter.submitList(messages);
            if (!messages.isEmpty()) {
                binding.recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
        viewModel.isLoading().observe(this, loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnSend.setEnabled(!loading);
        });
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                com.google.android.material.snackbar.Snackbar.make(
                        binding.getRoot(), error, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
