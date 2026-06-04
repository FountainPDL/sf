package com.surffountain.browser.models;

public class DownloadItem {
    public enum Status {
        PENDING, RUNNING, PAUSED, COMPLETED, FAILED, CANCELLED
    }

    public enum Category {
        IMAGE, VIDEO, AUDIO, DOCUMENT, APK, ARCHIVE, OTHER
    }

    private long id;
    private String url;
    private String filename;
    private String mimeType;
    private String filePath;
    private long totalBytes;
    private long downloadedBytes;
    private Status status;
    private Category category;
    private long startedAt;
    private long completedAt;
    private String errorMessage;
    private long downloadSpeed;
    private int threads;
    private boolean isScheduled;
    private long scheduledAt;
    private String sourceUrl;
    private String referer;

    public DownloadItem() {
        this.startedAt = System.currentTimeMillis();
        this.status = Status.PENDING;
        this.threads = 4;
    }

    public long getProgress() {
        if (totalBytes <= 0) return 0;
        return (downloadedBytes * 100) / totalBytes;
    }

    public String getFormattedSpeed() {
        if (downloadSpeed < 1024) return downloadSpeed + " B/s";
        if (downloadSpeed < 1024 * 1024) return (downloadSpeed / 1024) + " KB/s";
        return String.format("%.1f MB/s", downloadSpeed / (1024.0 * 1024.0));
    }

    public String getFormattedSize() {
        if (totalBytes < 1024) return totalBytes + " B";
        if (totalBytes < 1024 * 1024) return (totalBytes / 1024) + " KB";
        if (totalBytes < 1024 * 1024 * 1024) return String.format("%.1f MB", totalBytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", totalBytes / (1024.0 * 1024.0 * 1024.0));
    }

    public static Category categoryFromMimeType(String mimeType) {
        if (mimeType == null) return Category.OTHER;
        if (mimeType.startsWith("image/")) return Category.IMAGE;
        if (mimeType.startsWith("video/")) return Category.VIDEO;
        if (mimeType.startsWith("audio/")) return Category.AUDIO;
        if (mimeType.equals("application/pdf") || mimeType.startsWith("text/")) return Category.DOCUMENT;
        if (mimeType.equals("application/vnd.android.package-archive")) return Category.APK;
        if (mimeType.equals("application/zip") || mimeType.equals("application/x-rar-compressed")) return Category.ARCHIVE;
        return Category.OTHER;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }
    public long getDownloadedBytes() { return downloadedBytes; }
    public void setDownloadedBytes(long downloadedBytes) { this.downloadedBytes = downloadedBytes; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public long getStartedAt() { return startedAt; }
    public void setStartedAt(long startedAt) { this.startedAt = startedAt; }
    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getDownloadSpeed() { return downloadSpeed; }
    public void setDownloadSpeed(long downloadSpeed) { this.downloadSpeed = downloadSpeed; }
    public int getThreads() { return threads; }
    public void setThreads(int threads) { this.threads = threads; }
    public boolean isScheduled() { return isScheduled; }
    public void setScheduled(boolean scheduled) { isScheduled = scheduled; }
    public long getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(long scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getReferer() { return referer; }
    public void setReferer(String referer) { this.referer = referer; }
}
