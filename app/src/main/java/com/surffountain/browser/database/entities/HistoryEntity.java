package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "history",
    indices = {@Index("url"), @Index("visited_at")}
)
public class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "favicon_url")
    public String faviconUrl;

    @ColumnInfo(name = "visited_at")
    public long visitedAt;

    @ColumnInfo(name = "visit_count")
    public int visitCount;

    @ColumnInfo(name = "duration_seconds")
    public int durationSeconds;

    @ColumnInfo(name = "is_search")
    public boolean isSearch;

    @ColumnInfo(name = "search_query")
    public String searchQuery;

    @ColumnInfo(name = "is_synced")
    public boolean isSynced;

    @ColumnInfo(name = "device_name")
    public String deviceName;

    public HistoryEntity() {
        this.visitedAt = System.currentTimeMillis();
        this.visitCount = 1;
        this.isSynced = false;
    }
}
