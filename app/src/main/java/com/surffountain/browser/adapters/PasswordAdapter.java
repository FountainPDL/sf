package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.surffountain.browser.database.entities.PasswordEntity;
import com.surffountain.browser.databinding.ItemPasswordBinding;

public class PasswordAdapter extends ListAdapter<PasswordEntity, PasswordAdapter.ViewHolder> {

    public interface OnCopyListener { void onCopy(PasswordEntity item); }
    public interface OnDeleteListener { void onDelete(PasswordEntity item); }

    private final OnCopyListener copyListener;
    private final OnDeleteListener deleteListener;

    public PasswordAdapter(OnCopyListener copy, OnDeleteListener delete) {
        super(DIFF_CALLBACK);
        this.copyListener = copy;
        this.deleteListener = delete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPasswordBinding binding = ItemPasswordBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPasswordBinding binding;

        ViewHolder(ItemPasswordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PasswordEntity item) {
            binding.tvDomain.setText(item.domain);
            binding.tvUsername.setText(item.username);
            binding.tvPassword.setText("••••••••");

            if (item.faviconUrl != null) {
                Glide.with(binding.ivFavicon)
                        .load(item.faviconUrl)
                        .error(android.R.drawable.ic_menu_compass)
                        .into(binding.ivFavicon);
            }

            if (item.isCompromised) {
                binding.iconWarning.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.iconWarning.setVisibility(android.view.View.GONE);
            }

            binding.btnCopy.setOnClickListener(v -> copyListener.onCopy(item));
            binding.btnDelete.setOnClickListener(v -> deleteListener.onDelete(item));

            binding.btnTogglePassword.setOnClickListener(v -> {
                if (binding.tvPassword.getText().toString().equals("••••••••")) {
                    binding.tvPassword.setText("(tap Copy to see)");
                } else {
                    binding.tvPassword.setText("••••••••");
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<PasswordEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PasswordEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull PasswordEntity o, @NonNull PasswordEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull PasswordEntity o, @NonNull PasswordEntity n) {
                    return o.domain.equals(n.domain) && o.username.equals(n.username);
                }
            };
}
