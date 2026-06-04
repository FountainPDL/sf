package com.surffountain.browser.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.surffountain.browser.database.entities.HistoryEntity;
import com.surffountain.browser.databinding.ItemHistoryBinding;

public class HistoryAdapter extends ListAdapter<HistoryEntity, HistoryAdapter.ViewHolder> {

    public interface OnItemClickListener { void onClick(HistoryEntity item); }
    public interface OnDeleteClickListener { void onDelete(HistoryEntity item); }

    private final OnItemClickListener clickListener;
    private final OnDeleteClickListener deleteListener;

    public HistoryAdapter(OnItemClickListener click, OnDeleteClickListener delete) {
        super(DIFF_CALLBACK);
        this.clickListener = click;
        this.deleteListener = delete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryBinding binding;

        ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HistoryEntity item) {
            binding.tvTitle.setText(item.title != null ? item.title : item.url);
            binding.tvUrl.setText(item.url);
            binding.tvTime.setText(DateUtils.getRelativeTimeSpanString(item.visitedAt));

            if (item.faviconUrl != null) {
                Glide.with(binding.ivFavicon)
                        .load(item.faviconUrl)
                        .error(android.R.drawable.ic_menu_compass)
                        .into(binding.ivFavicon);
            }

            binding.getRoot().setOnClickListener(v -> clickListener.onClick(item));
            binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(item));
        }
    }

    private static final DiffUtil.ItemCallback<HistoryEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HistoryEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull HistoryEntity o, @NonNull HistoryEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull HistoryEntity o, @NonNull HistoryEntity n) {
                    return o.visitedAt == n.visitedAt;
                }
            };
}
