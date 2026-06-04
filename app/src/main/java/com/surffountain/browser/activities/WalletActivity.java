package com.surffountain.browser.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.surffountain.browser.R;
import com.surffountain.browser.databinding.ActivityWalletBinding;
import com.surffountain.browser.security.BiometricHelper;
import com.surffountain.browser.viewmodels.WalletViewModel;

public class WalletActivity extends AppCompatActivity {

    private ActivityWalletBinding binding;
    private WalletViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BiometricHelper bio = new BiometricHelper(this);
        bio.authenticate("Unlock Surf Wallet", "Authenticate to access your wallet",
            new BiometricHelper.AuthCallback() {
                @Override public void onSuccess() { initView(); }
                @Override public void onError(String e) { finish(); }
                @Override public void onFailed() {
                    Toast.makeText(WalletActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }

    private void initView() {
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Surf Wallet");
        }

        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        setupTabs();
        setupObservers();
        setupActions();
    }

    private void setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Portfolio"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("NFTs"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("DApps"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("History"));
    }

    private void setupObservers() {
        viewModel.getTotalBalance().observe(this, balance -> {
            binding.tvTotalBalance.setText(String.format("$%.2f", balance));
        });
        viewModel.getAddress().observe(this, address -> {
            binding.tvAddress.setText(address != null ? address : "No wallet connected");
        });
    }

    private void setupActions() {
        binding.btnSend.setOnClickListener(v ->
            Toast.makeText(this, "Send — connect your hardware wallet", Toast.LENGTH_SHORT).show());
        binding.btnReceive.setOnClickListener(v -> {
            String addr = viewModel.getAddress().getValue();
            if (addr != null) {
                // Show QR of address
                android.content.Intent i = new android.content.Intent(this, QrActivity.class);
                i.putExtra("generate", addr);
                startActivity(i);
            }
        });
        binding.btnConnectWallet.setOnClickListener(v -> viewModel.connectWallet(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
