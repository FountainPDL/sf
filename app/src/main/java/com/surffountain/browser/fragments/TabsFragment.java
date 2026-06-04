package com.surffountain.browser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.surffountain.browser.R;
import com.surffountain.browser.adapters.TabAdapter;
import com.surffountain.browser.databinding.FragmentTabsBinding;
import com.surffountain.browser.models.BrowserTab;
import com.surffountain.browser.webview.TabManager;

public class TabsFragment extends BottomSheetDialogFragment {

    private FragmentTabsBinding binding;
    private TabManager tabManager;
    private TabAdapter tabAdapter;
    private boolean isGridLayout = true;

    public static TabsFragment newInstance() {
        return new TabsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTabsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabManager = TabManager.getInstance(requireContext());

        setupRecyclerView();
        setupButtons();
        observeTabs();
    }

    private void setupRecyclerView() {
        tabAdapter = new TabAdapter(
            tab -> {
                tabManager.setActiveTab(tab);
                dismiss();
            },
            tab -> {
                tabManager.closeTab(tab);
            }
        );
        binding.recyclerTabs.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerTabs.setAdapter(tabAdapter);
    }

    private void setupButtons() {
        binding.btnNewTab.setOnClickListener(v -> {
            BrowserTab tab = tabManager.createTab("surf://home", false);
            tabManager.setActiveTab(tab);
            dismiss();
        });

        binding.btnNewIncognito.setOnClickListener(v -> {
            BrowserTab tab = tabManager.createTab("surf://home", true);
            tabManager.setActiveTab(tab);
            dismiss();
        });

        binding.btnCloseAll.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Close All Tabs")
                    .setMessage("Close all " + tabManager.getTabCount() + " tabs?")
                    .setPositiveButton("Close All", (d, w) -> {
                        tabManager.closeAllTabs(false);
                        dismiss();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.btnToggleLayout.setOnClickListener(v -> {
            isGridLayout = !isGridLayout;
            binding.recyclerTabs.setLayoutManager(isGridLayout ?
                    new GridLayoutManager(requireContext(), 2) :
                    new LinearLayoutManager(requireContext()));
            binding.btnToggleLayout.setIconResource(isGridLayout ?
                    R.drawable.ic_list : R.drawable.ic_grid);
        });

        binding.btnRestoreTab.setOnClickListener(v -> {
            BrowserTab restored = tabManager.restoreLastClosed();
            if (restored != null) {
                tabManager.setActiveTab(restored);
                dismiss();
            }
        });
    }

    private void observeTabs() {
        tabManager.getTabsLiveData().observe(getViewLifecycleOwner(), tabs -> {
            tabAdapter.submitList(tabs);
            binding.tvTabCount.setText(tabs.size() + " tabs");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
