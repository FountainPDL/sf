package com.surffountain.browser.webview;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.surffountain.browser.models.BrowserTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class TabManager {

    private static TabManager instance;

    private final MutableLiveData<List<BrowserTab>> tabs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<BrowserTab> activeTab = new MutableLiveData<>();
    private final Stack<BrowserTab> recentlyClosed = new Stack<>();
    private final Map<String, SurfWebView> webViews = new HashMap<>();
    private final Context context;

    private static final int MAX_RECENTLY_CLOSED = 20;
    private static final int THUMBNAIL_WIDTH = 360;
    private static final int THUMBNAIL_HEIGHT = 240;

    private TabManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static TabManager getInstance(Context context) {
        if (instance == null) {
            synchronized (TabManager.class) {
                if (instance == null) {
                    instance = new TabManager(context);
                }
            }
        }
        return instance;
    }

    public BrowserTab createTab(String url, boolean isIncognito) {
        BrowserTab tab = new BrowserTab(url, isIncognito);
        List<BrowserTab> current = new ArrayList<>(getTabs());
        current.add(tab);
        tabs.postValue(current);
        return tab;
    }

    public BrowserTab createTabFromParent(String url, BrowserTab parent) {
        BrowserTab tab = createTab(url, parent.isIncognito());
        tab.setParentTabId(parent.getId());
        tab.setGroupId(parent.getGroupId());
        return tab;
    }

    public BrowserTab duplicateTab(BrowserTab source) {
        BrowserTab dupe = new BrowserTab(source.getUrl(), source.isIncognito());
        dupe.setTitle(source.getTitle());
        dupe.setGroupId(source.getGroupId());
        List<BrowserTab> current = new ArrayList<>(getTabs());
        int idx = current.indexOf(source);
        current.add(idx + 1, dupe);
        tabs.postValue(current);
        return dupe;
    }

    public void closeTab(BrowserTab tab) {
        List<BrowserTab> current = new ArrayList<>(getTabs());
        int idx = current.indexOf(tab);
        current.remove(tab);

        // Save to recently closed
        if (!tab.isIncognito()) {
            recentlyClosed.push(tab);
            if (recentlyClosed.size() > MAX_RECENTLY_CLOSED) {
                recentlyClosed.remove(0);
            }
        }

        // Destroy webview
        SurfWebView wv = webViews.remove(tab.getId());
        if (wv != null) {
            wv.destroy();
        }

        tabs.postValue(current);

        // Select adjacent tab
        if (!current.isEmpty()) {
            int newIdx = Math.min(idx, current.size() - 1);
            setActiveTab(current.get(newIdx));
        } else {
            activeTab.postValue(null);
        }
    }

    public void closeAllTabs(boolean incognitoOnly) {
        List<BrowserTab> current = new ArrayList<>(getTabs());
        List<BrowserTab> toClose = new ArrayList<>();
        for (BrowserTab t : current) {
            if (!incognitoOnly || t.isIncognito()) toClose.add(t);
        }
        for (BrowserTab t : toClose) {
            current.remove(t);
            SurfWebView wv = webViews.remove(t.getId());
            if (wv != null) wv.destroy();
        }
        tabs.postValue(current);
        if (!current.isEmpty()) setActiveTab(current.get(0));
        else activeTab.postValue(null);
    }

    public BrowserTab restoreLastClosed() {
        if (recentlyClosed.isEmpty()) return null;
        BrowserTab tab = recentlyClosed.pop();
        List<BrowserTab> current = new ArrayList<>(getTabs());
        current.add(tab);
        tabs.postValue(current);
        return tab;
    }

    public void setActiveTab(BrowserTab tab) {
        List<BrowserTab> current = new ArrayList<>(getTabs());
        for (BrowserTab t : current) {
            t.setActive(t.getId().equals(tab.getId()));
        }
        tab.setLastAccessedAt(System.currentTimeMillis());
        tabs.postValue(current);
        activeTab.postValue(tab);
    }

    public void moveTab(int fromIndex, int toIndex) {
        List<BrowserTab> current = new ArrayList<>(getTabs());
        if (fromIndex < 0 || fromIndex >= current.size() || toIndex < 0 || toIndex >= current.size()) return;
        BrowserTab tab = current.remove(fromIndex);
        current.add(toIndex, tab);
        tabs.postValue(current);
    }

    public void pinTab(BrowserTab tab) {
        tab.setPinned(!tab.isPinned());
        // Re-sort: pinned tabs go first
        List<BrowserTab> current = new ArrayList<>(getTabs());
        List<BrowserTab> pinned = current.stream().filter(BrowserTab::isPinned).collect(Collectors.toList());
        List<BrowserTab> unpinned = current.stream().filter(t -> !t.isPinned()).collect(Collectors.toList());
        List<BrowserTab> sorted = new ArrayList<>();
        sorted.addAll(pinned);
        sorted.addAll(unpinned);
        tabs.postValue(sorted);
    }

    public List<BrowserTab> searchTabs(String query) {
        String lower = query.toLowerCase();
        List<BrowserTab> all = getTabs();
        List<BrowserTab> result = new ArrayList<>();
        for (BrowserTab t : all) {
            if ((t.getTitle() != null && t.getTitle().toLowerCase().contains(lower)) ||
                (t.getUrl() != null && t.getUrl().toLowerCase().contains(lower))) {
                result.add(t);
            }
        }
        return result;
    }

    public void captureThumbnail(BrowserTab tab) {
        SurfWebView wv = webViews.get(tab.getId());
        if (wv != null) {
            Bitmap thumb = wv.captureThumbnail(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            tab.setThumbnail(thumb);
        }
    }

    public void setGroupForTab(BrowserTab tab, String groupId) {
        tab.setGroupId(groupId);
        tabs.postValue(new ArrayList<>(getTabs()));
    }

    public List<BrowserTab> getTabsInGroup(String groupId) {
        List<BrowserTab> result = new ArrayList<>();
        for (BrowserTab t : getTabs()) {
            if (groupId.equals(t.getGroupId())) result.add(t);
        }
        return result;
    }

    public void registerWebView(String tabId, SurfWebView webView) {
        webViews.put(tabId, webView);
    }

    public SurfWebView getWebView(String tabId) {
        return webViews.get(tabId);
    }

    public void suspendBackgroundTab(BrowserTab tab) {
        tab.setBackground(true);
        SurfWebView wv = webViews.get(tab.getId());
        if (wv != null) {
            wv.onPause();
            wv.pauseTimers();
        }
    }

    public void resumeTab(BrowserTab tab) {
        tab.setBackground(false);
        SurfWebView wv = webViews.get(tab.getId());
        if (wv != null) {
            wv.onResume();
            wv.resumeTimers();
        }
    }

    private List<BrowserTab> getTabs() {
        List<BrowserTab> t = tabs.getValue();
        return t != null ? t : new ArrayList<>();
    }

    public LiveData<List<BrowserTab>> getTabsLiveData() { return tabs; }
    public LiveData<BrowserTab> getActiveTabLiveData() { return activeTab; }
    public int getTabCount() { return getTabs().size(); }
    public int getNormalTabCount() {
        int c = 0;
        for (BrowserTab t : getTabs()) if (!t.isIncognito()) c++;
        return c;
    }
    public int getIncognitoTabCount() {
        int c = 0;
        for (BrowserTab t : getTabs()) if (t.isIncognito()) c++;
        return c;
    }
    public Stack<BrowserTab> getRecentlyClosed() { return recentlyClosed; }
    public BrowserTab getActiveTab() { return activeTab.getValue(); }
}
