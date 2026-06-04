package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "speed_dial",
    indices = {@Index("position")}
)
public class SpeedDialEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "favicon_url")
    public String faviconUrl;

    @ColumnInfo(name = "thumbnail_path")
    public String thumbnailPath;

    @ColumnInfo(name = "position")
    public int position;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public SpeedDialEntity() {
        this.createdAt = System.currentTimeMillis();
    }
}
