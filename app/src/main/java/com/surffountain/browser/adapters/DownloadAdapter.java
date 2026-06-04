package com.surffountain.browser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.surffountain.browser.database.entities.DownloadEntity;
import com.surffountain.browser.databinding.ItemDownloadBinding;

public class DownloadAdapter extends ListAdapter<DownloadEntity, DownloadAdapter.ViewHolder> {

    public interface OnPauseResumeListener { void onPauseResume(DownloadEntity item); }
    public interface OnCancelListener { void onCancel(DownloadEntity item); }
    public interface OnRetryListener { void onRetry(DownloadEntity item); }
    public interface OnOpenListener { void onOpen(DownloadEntity item); }

    private final OnPauseResumeListener pauseResumeListener;
    private final OnCancelListener cancelListener;
    private final OnRetryListener retryListener;
    private final OnOpenListener openListener;

    public DownloadAdapter(OnPauseResumeListener pr, OnCancelListener c, OnRetryListener r, OnOpenListener o) {
        super(DIFF_CALLBACK);
        this.pauseResumeListener = pr;
        this.cancelListener = c;
        this.retryListener = r;
        this.openListener = o;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDownloadBinding binding = ItemDownloadBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDownloadBinding binding;

        ViewHolder(ItemDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DownloadEntity item) {
            binding.tvFilename.setText(item.filename);

            String sizeInfo = "";
            if (item.totalBytes > 0) {
                long progress = item.totalBytes > 0 ? (item.downloadedBytes * 100 / item.totalBytes) : 0;
                sizeInfo = formatSize(item.downloadedBytes) + " / " + formatSize(item.totalBytes) + " (" + progress + "%)";
                binding.progressBar.setProgress((int) progress);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
            binding.tvSize.setText(sizeInfo.isEmpty() ? item.status : sizeInfo + " · " + item.status);

            boolean isActive = "RUNNING".equals(item.status) || "PENDING".equals(item.status);
            boolean isPaused = "PAUSED".equals(item.status);
            boolean isCompleted = "COMPLETED".equals(item.status);
            boolean isFailed = "FAILED".equals(item.status) || "CANCELLED".equals(item.status);

            binding.btnAction.setVisibility(isActive || isPaused ? View.VISIBLE : View.GONE);
            binding.btnCancel.setVisibility(isActive || isPaused ? View.VISIBLE : View.GONE);

            if (isActive) {
                binding.btnAction.setIconResource(android.R.drawable.ic_media_pause);
            } else if (isPaused) {
                binding.btnAction.setIconResource(android.R.drawable.ic_media_play);
            }

            binding.btnAction.setOnClickListener(v -> pauseResumeListener.onPauseResume(item));
            binding.btnCancel.setOnClickListener(v -> {
                if (isFailed) retryListener.onRetry(item);
                else cancelListener.onCancel(item);
            });
            binding.getRoot().setOnClickListener(v -> {
                if (isCompleted) openListener.onOpen(item);
            });
        }

        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + "B";
            if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
            return String.format("%.1fMB", bytes / (1024.0 * 1024.0));
        }
    }

    private static final DiffUtil.ItemCallback<DownloadEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DownloadEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull DownloadEntity o, @NonNull DownloadEntity n) {
                    return o.id == n.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull DownloadEntity o, @NonNull DownloadEntity n) {
                    return o.status.equals(n.status) && o.downloadedBytes == n.downloadedBytes;
                }
            };
}
