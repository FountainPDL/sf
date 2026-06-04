package com.surffountain.browser.activities;

import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.surffountain.browser.R;
import com.surffountain.browser.databinding.ActivityVpnBinding;
import com.surffountain.browser.viewmodels.VpnViewModel;

public class VpnActivity extends AppCompatActivity {

    private ActivityVpnBinding binding;
    private VpnViewModel viewModel;

    private ActivityResultLauncher<Intent> vpnPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVpnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Surf VPN");
        }

        vpnPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        viewModel.startVpn(this);
                    }
                }
        );

        viewModel = new ViewModelProvider(this).get(VpnViewModel.class);
        setupUI();
        setupObservers();
    }

    private void setupUI() {
        binding.btnToggleVpn.setOnClickListener(v -> {
            if (viewModel.isConnected().getValue() == Boolean.TRUE) {
                viewModel.stopVpn(this);
            } else {
                Intent vpnIntent = VpnService.prepare(this);
                if (vpnIntent != null) {
                    vpnPermissionLauncher.launch(vpnIntent);
                } else {
                    viewModel.startVpn(this);
                }
            }
        });

        // Region selection
        String[] regions = {"Auto", "US", "UK", "DE", "FR", "JP", "AU", "CA", "NL", "CH"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, regions);
        binding.spinnerRegion.setAdapter(adapter);
        binding.spinnerRegion.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                viewModel.setRegion(regions[pos]);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        binding.switchKillSwitch.setOnCheckedChangeListener((btn, checked) -> viewModel.setKillSwitch(checked));
        binding.switchAutoConnect.setOnCheckedChangeListener((btn, checked) -> viewModel.setAutoConnect(checked));
    }

    private void setupObservers() {
        viewModel.isConnected().observe(this, connected -> {
            if (connected) {
                binding.btnToggleVpn.setText("Disconnect");
                binding.tvStatus.setText("Connected");
                binding.tvStatus.setTextColor(0xFF4CAF50);
                binding.indicatorDot.setColorFilter(0xFF4CAF50);
            } else {
                binding.btnToggleVpn.setText("Connect");
                binding.tvStatus.setText("Disconnected");
                binding.tvStatus.setTextColor(0xFFE53935);
                binding.indicatorDot.setColorFilter(0xFFE53935);
            }
        });
        viewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
