package com.surffountain.browser.models;

import java.util.ArrayList;
import java.util.List;

public class BrowserSession {
    private String id;
    private String name;
    private List<String> tabIds;
    private String activeTabId;
    private long savedAt;
    private boolean isIncognito;
    private String deviceName;

    public BrowserSession() {
        this.tabIds = new ArrayList<>();
        this.savedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getTabIds() { return tabIds; }
    public void setTabIds(List<String> tabIds) { this.tabIds = tabIds; }
    public void addTabId(String tabId) { this.tabIds.add(tabId); }
    public String getActiveTabId() { return activeTabId; }
    public void setActiveTabId(String activeTabId) { this.activeTabId = activeTabId; }
    public long getSavedAt() { return savedAt; }
    public void setSavedAt(long savedAt) { this.savedAt = savedAt; }
    public boolean isIncognito() { return isIncognito; }
    public void setIncognito(boolean incognito) { isIncognito = incognito; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
}
