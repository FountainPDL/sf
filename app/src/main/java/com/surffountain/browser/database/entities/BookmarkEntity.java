package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "bookmarks",
    foreignKeys = @ForeignKey(
        entity = BookmarkFolderEntity.class,
        parentColumns = "id",
        childColumns = "folder_id",
        onDelete = ForeignKey.SET_NULL
    ),
    indices = {@Index("folder_id"), @Index("url")}
)
public class BookmarkEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "folder_id")
    public Long folderId;

    @ColumnInfo(name = "favicon_url")
    public String faviconUrl;

    @ColumnInfo(name = "thumbnail_path")
    public String thumbnailPath;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "modified_at")
    public long modifiedAt;

    @ColumnInfo(name = "visit_count")
    public int visitCount;

    @ColumnInfo(name = "is_synced")
    public boolean isSynced;

    @ColumnInfo(name = "sync_id")
    public String syncId;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    public BookmarkEntity() {
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
        this.visitCount = 0;
        this.isSynced = false;
    }
}
