package com.surffountain.browser.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "passwords",
    indices = {@Index("domain"), @Index("username")}
)
public class PasswordEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "domain")
    public String domain;

    @ColumnInfo(name = "username")
    public String username;

    /** AES-256 encrypted, stored as Base64. Key lives in Android Keystore. */
    @ColumnInfo(name = "encrypted_password")
    public String encryptedPassword;

    @ColumnInfo(name = "encryption_iv")
    public String encryptionIv;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "modified_at")
    public long modifiedAt;

    @ColumnInfo(name = "last_used_at")
    public long lastUsedAt;

    @ColumnInfo(name = "use_count")
    public int useCount;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "is_synced")
    public boolean isSynced;

    @ColumnInfo(name = "strength_score")
    public int strengthScore;

    @ColumnInfo(name = "is_compromised")
    public boolean isCompromised;

    @ColumnInfo(name = "favicon_url")
    public String faviconUrl;

    public PasswordEntity() {
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
        this.lastUsedAt = System.currentTimeMillis();
        this.useCount = 0;
        this.isSynced = false;
        this.isCompromised = false;
    }
}
