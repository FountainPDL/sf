package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.surffountain.browser.database.entities.BookmarkEntity;
import com.surffountain.browser.databinding.ItemBookmarkBinding;

public class BookmarkAdapter extends ListAdapter<BookmarkEntity, BookmarkAdapter.ViewHolder> {

    public interface OnItemClickListener { void onClick(BookmarkEntity item); }
    public interface OnItemLongClickListener { void onLongClick(BookmarkEntity item); }

    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    public BookmarkAdapter(OnItemClickListener click, OnItemLongClickListener longClick) {
        super(DIFF_CALLBACK);
        this.clickListener = click;
        this.longClickListener = longClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookmarkBinding binding = ItemBookmarkBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookmarkBinding binding;

        ViewHolder(ItemBookmarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BookmarkEntity item) {
            binding.tvTitle.setText(item.title);
            binding.tvUrl.setText(item.url);

            if (item.faviconUrl != null) {
                Glide.with(binding.ivFavicon)
                        .load(item.faviconUrl)
                        .error(android.R.drawable.ic_menu_compass)
                        .into(binding.ivFavicon);
            }

            binding.getRoot().setOnClickListener(v -> clickListener.onClick(item));
            binding.getRoot().setOnLongClickListener(v -> { longClickListener.onLongClick(item); return true; });
        }
    }

    private static final DiffUtil.ItemCallback<BookmarkEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<BookmarkEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull BookmarkEntity o, @NonNull BookmarkEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull BookmarkEntity o, @NonNull BookmarkEntity n) {
                    return o.title.equals(n.title) && o.url.equals(n.url);
                }
            };
}
