package com.surffountain.browser.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.surffountain.browser.R;
import com.surffountain.browser.databinding.FragmentAddressBarBinding;
import com.surffountain.browser.utils.UrlUtils;

public class AddressBarFragment extends Fragment {

    public interface AddressBarListener {
        void onUrlSubmitted(String input);
        void onVoiceSearch();
        void onNewIncognitoTab();
        void onSearchEngineChanged(String engineId);
    }

    private FragmentAddressBarBinding binding;
    private AddressBarListener listener;
    private String currentUrl;

    public static AddressBarFragment newInstance() {
        return new AddressBarFragment();
    }

    public void setListener(AddressBarListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAddressInput();
        setupButtons();
    }

    private void setupAddressInput() {
        binding.etAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitInput();
                return true;
            }
            return false;
        });

        binding.etAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.etAddress.selectAll();
                binding.btnVoiceSearch.setVisibility(View.VISIBLE);
            } else {
                binding.btnVoiceSearch.setVisibility(View.GONE);
                if (currentUrl != null) {
                    binding.etAddress.setText(UrlUtils.getDisplayUrl(currentUrl));
                }
                dismissKeyboard();
            }
        });

        binding.etAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupButtons() {
        binding.btnClear.setOnClickListener(v -> binding.etAddress.setText(""));
        binding.btnVoiceSearch.setOnClickListener(v -> {
            if (listener != null) listener.onVoiceSearch();
        });
        binding.btnShield.setOnClickListener(v -> showShieldInfo());
    }

    private void submitInput() {
        String input = binding.etAddress.getText().toString().trim();
        if (!input.isEmpty() && listener != null) {
            listener.onUrlSubmitted(input);
            binding.etAddress.clearFocus();
            dismissKeyboard();
        }
    }

    public void setUrl(String url) {
        currentUrl = url;
        if (binding != null && !binding.etAddress.hasFocus()) {
            boolean isHttps = UrlUtils.isHttps(url);
            binding.btnShield.setImageResource(isHttps ? R.drawable.ic_shield_secure : R.drawable.ic_shield_warning);
            binding.etAddress.setText(UrlUtils.getDisplayUrl(url));
        }
    }

    private void showShieldInfo() {
        // Show privacy info for current site
        com.surffountain.browser.SurfFountainApp app = com.surffountain.browser.SurfFountainApp.getInstance();
        com.surffountain.browser.models.PrivacyStats stats = app.getSettingsManager().getPrivacyStats();
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Surf Shield")
                .setMessage("Ads blocked: " + stats.getAdsBlocked() +
                        "\nTrackers blocked: " + stats.getTrackersBlocked() +
                        "\nHTTPS upgrades: " + stats.getHttpsUpgrades())
                .setPositiveButton("Privacy Center", (d, w) -> {
                    startActivity(new android.content.Intent(requireContext(),
                            com.surffountain.browser.activities.PrivacyCenterActivity.class));
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void dismissKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null && binding != null)
                imm.hideSoftInputFromWindow(binding.etAddress.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
