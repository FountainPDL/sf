package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.surffountain.browser.database.entities.SpeedDialEntity;
import com.surffountain.browser.databinding.ItemSpeedDialBinding;

public class SpeedDialAdapter extends ListAdapter<SpeedDialEntity, SpeedDialAdapter.ViewHolder> {

    public interface OnItemClickListener { void onClick(SpeedDialEntity item); }
    public interface OnItemLongClickListener { void onLongClick(SpeedDialEntity item); }

    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    public SpeedDialAdapter(OnItemClickListener click, OnItemLongClickListener longClick) {
        super(DIFF_CALLBACK);
        this.clickListener = click;
        this.longClickListener = longClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSpeedDialBinding binding = ItemSpeedDialBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSpeedDialBinding binding;

        ViewHolder(ItemSpeedDialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SpeedDialEntity item) {
            binding.tvTitle.setText(item.title);

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

    private static final DiffUtil.ItemCallback<SpeedDialEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SpeedDialEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull SpeedDialEntity o, @NonNull SpeedDialEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull SpeedDialEntity o, @NonNull SpeedDialEntity n) {
                    return o.title.equals(n.title) && o.url.equals(n.url);
                }
            };
}
