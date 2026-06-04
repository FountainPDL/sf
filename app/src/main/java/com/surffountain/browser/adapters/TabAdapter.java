package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.surffountain.browser.databinding.ItemTabBinding;
import com.surffountain.browser.models.BrowserTab;

public class TabAdapter extends ListAdapter<BrowserTab, TabAdapter.ViewHolder> {

    public interface OnTabClickListener { void onTabClick(BrowserTab tab); }
    public interface OnTabCloseListener { void onTabClose(BrowserTab tab); }

    private final OnTabClickListener clickListener;
    private final OnTabCloseListener closeListener;

    public TabAdapter(OnTabClickListener click, OnTabCloseListener close) {
        super(DIFF_CALLBACK);
        this.clickListener = click;
        this.closeListener = close;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTabBinding binding = ItemTabBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTabBinding binding;

        ViewHolder(ItemTabBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BrowserTab tab) {
            binding.tvTitle.setText(tab.getTitle() != null ? tab.getTitle() : tab.getUrl());

            if (tab.getThumbnail() != null) {
                binding.ivThumbnail.setImageBitmap(tab.getThumbnail());
            } else {
                binding.ivThumbnail.setImageResource(android.R.color.darker_gray);
            }

            if (tab.getFavicon() != null) {
                binding.ivFavicon.setImageBitmap(tab.getFavicon());
            } else if (tab.getUrl() != null && !tab.isHomePage()) {
                Glide.with(binding.ivFavicon)
                        .load("https://www.google.com/s2/favicons?domain=" +
                                com.surffountain.browser.utils.UrlUtils.getDomain(tab.getUrl()) + "&sz=32")
                        .into(binding.ivFavicon);
            }

            binding.chipIncognito.setVisibility(tab.isIncognito() ? View.VISIBLE : View.GONE);
            binding.iconPinned.setVisibility(tab.isPinned() ? View.VISIBLE : View.GONE);

            // Highlight active tab
            binding.getRoot().setCardBackgroundColor(tab.isActive() ?
                    0x200096C7 : // Light primary tint
                    0x00000000);
            binding.getRoot().setStrokeWidth(tab.isActive() ? 2 : 0);

            binding.getRoot().setOnClickListener(v -> clickListener.onTabClick(tab));
            binding.btnClose.setOnClickListener(v -> closeListener.onTabClose(tab));
        }
    }

    private static final DiffUtil.ItemCallback<BrowserTab> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BrowserTab>() {
                @Override
                public boolean areItemsTheSame(@NonNull BrowserTab o, @NonNull BrowserTab n) {
                    return o.getId().equals(n.getId());
                }
                @Override
                public boolean areContentsTheSame(@NonNull BrowserTab o, @NonNull BrowserTab n) {
                    return o.getTitle() != null && o.getTitle().equals(n.getTitle()) &&
                           o.isActive() == n.isActive() && o.isLoading() == n.isLoading();
                }
            };
}
