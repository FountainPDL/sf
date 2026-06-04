package com.surffountain.browser.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.surffountain.browser.R;
import com.surffountain.browser.adapters.PasswordAdapter;
import com.surffountain.browser.databinding.ActivityPasswordManagerBinding;
import com.surffountain.browser.security.BiometricHelper;
import com.surffountain.browser.viewmodels.PasswordViewModel;

public class PasswordManagerActivity extends AppCompatActivity {

    private ActivityPasswordManagerBinding binding;
    private PasswordViewModel viewModel;
    private PasswordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Biometric lock for password manager
        BiometricHelper bio = new BiometricHelper(this);
        bio.authenticate("Unlock Password Manager", "Authenticate to view passwords",
            new BiometricHelper.AuthCallback() {
                @Override public void onSuccess() { initView(); }
                @Override public void onError(String e) { finish(); }
                @Override public void onFailed() { finish(); }
            });
    }

    private void initView() {
        binding = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Password Manager");
        }

        viewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        setupRecyclerView();
        setupSearch();
        setupObservers();
        setupFab();
    }

    private void setupRecyclerView() {
        adapter = new PasswordAdapter(
            item -> viewModel.copyPassword(item, this),
            item -> viewModel.delete(item)
        );
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
        viewModel.getPasswords().observe(this, passwords -> {
            adapter.submitList(passwords);
            binding.emptyView.setVisibility(passwords.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupFab() {
        binding.fabGenerate.setOnClickListener(v -> {
            String pw = viewModel.generatePassword(16, true, true, true, true);
            binding.tvGeneratedPassword.setText(pw);
            binding.cardGeneratedPassword.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
