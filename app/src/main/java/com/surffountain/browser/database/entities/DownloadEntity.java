package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "downloads",
    indices = {@Index("status"), @Index("started_at")}
)
public class DownloadEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "filename")
    public String filename;

    @ColumnInfo(name = "mime_type")
    public String mimeType;

    @ColumnInfo(name = "file_path")
    public String filePath;

    @ColumnInfo(name = "total_bytes")
    public long totalBytes;

    @ColumnInfo(name = "downloaded_bytes")
    public long downloadedBytes;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "started_at")
    public long startedAt;

    @ColumnInfo(name = "completed_at")
    public long completedAt;

    @ColumnInfo(name = "error_message")
    public String errorMessage;

    @ColumnInfo(name = "threads")
    public int threads;

    @ColumnInfo(name = "source_url")
    public String sourceUrl;

    @ColumnInfo(name = "referer")
    public String referer;

    @ColumnInfo(name = "work_id")
    public String workId;

    public DownloadEntity() {
        this.startedAt = System.currentTimeMillis();
        this.status = "PENDING";
        this.threads = 4;
    }
}
