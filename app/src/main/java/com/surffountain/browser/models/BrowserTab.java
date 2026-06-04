package com.surffountain.browser.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class BrowserTab implements Parcelable {

    public enum State {
        LOADING, LOADED, ERROR, CRASHED
    }

    private final String id;
    private String url;
    private String title;
    private Bitmap favicon;
    private Bitmap thumbnail;
    private boolean isIncognito;
    private boolean isPinned;
    private boolean isMuted;
    private boolean isActive;
    private boolean isLoading;
    private State state;
    private long createdAt;
    private long lastAccessedAt;
    private String groupId;
    private int progress;
    private String parentTabId;
    private boolean isBackground;

    public BrowserTab(String url, boolean isIncognito) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.isIncognito = isIncognito;
        this.title = url;
        this.state = State.LOADING;
        this.createdAt = System.currentTimeMillis();
        this.lastAccessedAt = System.currentTimeMillis();
        this.progress = 0;
        this.isBackground = false;
    }

    protected BrowserTab(Parcel in) {
        id = in.readString();
        url = in.readString();
        title = in.readString();
        isIncognito = in.readByte() != 0;
        isPinned = in.readByte() != 0;
        isMuted = in.readByte() != 0;
        isActive = in.readByte() != 0;
        isLoading = in.readByte() != 0;
        state = State.values()[in.readInt()];
        createdAt = in.readLong();
        lastAccessedAt = in.readLong();
        groupId = in.readString();
        progress = in.readInt();
        parentTabId = in.readString();
        isBackground = in.readByte() != 0;
    }

    public static final Creator<BrowserTab> CREATOR = new Creator<BrowserTab>() {
        @Override
        public BrowserTab createFromParcel(Parcel in) {
            return new BrowserTab(in);
        }

        @Override
        public BrowserTab[] newArray(int size) {
            return new BrowserTab[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeByte((byte) (isIncognito ? 1 : 0));
        dest.writeByte((byte) (isPinned ? 1 : 0));
        dest.writeByte((byte) (isMuted ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isLoading ? 1 : 0));
        dest.writeInt(state.ordinal());
        dest.writeLong(createdAt);
        dest.writeLong(lastAccessedAt);
        dest.writeString(groupId);
        dest.writeInt(progress);
        dest.writeString(parentTabId);
        dest.writeByte((byte) (isBackground ? 1 : 0));
    }

    @Override
    public int describeContents() { return 0; }

    // Getters and setters
    public String getId() { return id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Bitmap getFavicon() { return favicon; }
    public void setFavicon(Bitmap favicon) { this.favicon = favicon; }
    public Bitmap getThumbnail() { return thumbnail; }
    public void setThumbnail(Bitmap thumbnail) { this.thumbnail = thumbnail; }
    public boolean isIncognito() { return isIncognito; }
    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
    public boolean isMuted() { return isMuted; }
    public void setMuted(boolean muted) { isMuted = muted; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public boolean isLoading() { return isLoading; }
    public void setLoading(boolean loading) { isLoading = loading; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public long getCreatedAt() { return createdAt; }
    public long getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(long lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getParentTabId() { return parentTabId; }
    public void setParentTabId(String parentTabId) { this.parentTabId = parentTabId; }
    public boolean isBackground() { return isBackground; }
    public void setBackground(boolean background) { isBackground = background; }

    public boolean isHomePage() {
        return url == null || url.isEmpty() || url.equals("about:blank") || url.startsWith("surf://");
    }
}
