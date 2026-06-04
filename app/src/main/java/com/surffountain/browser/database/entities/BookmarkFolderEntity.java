package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "bookmark_folders",
    foreignKeys = @ForeignKey(
        entity = BookmarkFolderEntity.class,
        parentColumns = "id",
        childColumns = "parent_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("parent_id")}
)
public class BookmarkFolderEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "parent_id")
    public Long parentId;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "modified_at")
    public long modifiedAt;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "is_synced")
    public boolean isSynced;

    @ColumnInfo(name = "sync_id")
    public String syncId;

    public BookmarkFolderEntity() {
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
    }
}
