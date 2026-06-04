package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "tab_sessions",
    indices = {@Index("saved_at")}
)
public class TabSessionEntity {

    @PrimaryKey
    public String id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "favicon_url")
    public String faviconUrl;

    @ColumnInfo(name = "saved_at")
    public long savedAt;

    @ColumnInfo(name = "position")
    public int position;

    @ColumnInfo(name = "is_pinned")
    public boolean isPinned;

    @ColumnInfo(name = "group_id")
    public String groupId;

    @ColumnInfo(name = "group_name")
    public String groupName;

    @ColumnInfo(name = "group_color")
    public int groupColor;

    @ColumnInfo(name = "scroll_x")
    public int scrollX;

    @ColumnInfo(name = "scroll_y")
    public int scrollY;

    @ColumnInfo(name = "back_stack")
    public String backStack;

    public TabSessionEntity() {
        this.savedAt = System.currentTimeMillis();
    }
}
