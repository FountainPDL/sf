package com.surffountain.browser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.surffountain.browser.R;
import com.surffountain.browser.adapters.BookmarkAdapter;
import com.surffountain.browser.database.entities.BookmarkEntity;
import com.surffountain.browser.databinding.ActivityBookmarksBinding;
import com.surffountain.browser.viewmodels.BookmarkViewModel;

public class BookmarksActivity extends AppCompatActivity {

    private ActivityBookmarksBinding binding;
    private BookmarkViewModel viewModel;
    private BookmarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookmarksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bookmarks);
        }

        viewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        setupRecyclerView();
        setupSearch();
        setupObservers();
        setupFab();
    }

    private void setupRecyclerView() {
        adapter = new BookmarkAdapter(item -> {
            // Open bookmark in browser
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(item.url));
            startActivity(intent);
            finish();
        }, item -> {
            // Long press - show options
            showBookmarkOptions(item);
        });
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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
        viewModel.getBookmarks().observe(this, bookmarks -> {
            adapter.submitList(bookmarks);
            binding.emptyView.setVisibility(bookmarks.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupFab() {
        binding.fabAddFolder.setOnClickListener(v -> viewModel.createFolder("New Folder", null));
    }

    private void showBookmarkOptions(BookmarkEntity item) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(item.title)
                .setItems(new String[]{"Edit", "Delete", "Share"}, (d, which) -> {
                    switch (which) {
                        case 0: showEditDialog(item); break;
                        case 1: viewModel.delete(item); break;
                        case 2:
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, item.url);
                            startActivity(Intent.createChooser(share, "Share"));
                            break;
                    }
                }).show();
    }

    private void showEditDialog(BookmarkEntity item) {
        android.widget.EditText titleInput = new android.widget.EditText(this);
        titleInput.setText(item.title);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Edit Bookmark")
                .setView(titleInput)
                .setPositiveButton("Save", (d, w) -> {
                    item.title = titleInput.getText().toString();
                    viewModel.update(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
