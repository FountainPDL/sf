package com.surffountain.browser.fragments;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.surffountain.browser.R;
import com.surffountain.browser.adapters.SpeedDialAdapter;
import com.surffountain.browser.databinding.FragmentHomeBinding;
import com.surffountain.browser.viewmodels.HomeViewModel;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private SpeedDialAdapter speedDialAdapter;
    private Timer clockTimer;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setupClock();
        setupSpeedDial();
        setupObservers();
    }

    private void setupClock() {
        updateClock();
        clockTimer = new Timer();
        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                if (getActivity() != null) getActivity().runOnUiThread(() -> updateClock());
            }
        }, 0, 1000);
    }

    private void updateClock() {
        if (binding == null) return;
        Date now = new Date();
        binding.tvTime.setText(DateFormat.getTimeFormat(requireContext()).format(now));
        binding.tvDate.setText(DateFormat.getLongDateFormat(requireContext()).format(now));
    }

    private void setupSpeedDial() {
        speedDialAdapter = new SpeedDialAdapter(item -> {
            if (getActivity() instanceof com.surffountain.browser.activities.MainActivity) {
                // Open URL from speed dial
                android.content.Intent intent = new android.content.Intent(
                        getActivity(), com.surffountain.browser.activities.MainActivity.class);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(item.url));
                startActivity(intent);
            }
        }, item -> {
            // Long press - edit/delete
            viewModel.deleteSpeedDial(item);
        });

        binding.recyclerSpeedDial.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        binding.recyclerSpeedDial.setAdapter(speedDialAdapter);

        binding.btnAddSpeedDial.setOnClickListener(v -> showAddSpeedDialDialog());
    }

    private void setupObservers() {
        viewModel.getSpeedDials().observe(getViewLifecycleOwner(), items -> {
            speedDialAdapter.submitList(items);
        });
    }

    private void showAddSpeedDialDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_speed_dial, null);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Speed Dial")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String url = ((android.widget.EditText) dialogView.findViewById(R.id.et_url)).getText().toString();
                    String title = ((android.widget.EditText) dialogView.findViewById(R.id.et_title)).getText().toString();
                    viewModel.addSpeedDial(url, title);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (clockTimer != null) clockTimer.cancel();
        binding = null;
    }
}
