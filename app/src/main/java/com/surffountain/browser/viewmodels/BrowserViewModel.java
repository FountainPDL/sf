package com.surffountain.browser.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.HistoryEntity;
import com.surffountain.browser.database.entities.BookmarkEntity;
import com.surffountain.browser.database.entities.TabSessionEntity;
import com.surffountain.browser.models.BrowserTab;
import com.surffountain.browser.webview.TabManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrowserViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MutableLiveData<List<String>> restoredTabs = new MutableLiveData<>();
    private AppDatabase db;

    public BrowserViewModel() {
        db = SurfFountainApp.getInstance().getDatabase();
    }

    public void saveToHistory(String url, String title) {
        if (url == null || url.isEmpty() || url.startsWith("surf://") || url.equals("about:blank")) return;
        executor.execute(() -> {
            HistoryEntity existing = db.historyDao().getByUrl(url);
            if (existing != null) {
                db.historyDao().updateVisit(url, System.currentTimeMillis());
            } else {
                HistoryEntity history = new HistoryEntity();
                history.url = url;
                history.title = title;
                db.historyDao().insert(history);
            }
        });
    }

    public void addBookmark(String url, String title) {
        executor.execute(() -> {
            BookmarkEntity bookmark = new BookmarkEntity();
            bookmark.url = url;
            bookmark.title = title != null ? title : url;
            db.bookmarkDao().insert(bookmark);
        });
    }

    public void promptPasswordSave(String url, String credentialsJson) {
        // Parse credentials and show save dialog via LiveData event
    }

    public void saveSession(TabManager tabManager, Context context) {
        executor.execute(() -> {
            List<BrowserTab> tabs = tabManager.getTabsLiveData().getValue();
            if (tabs == null) return;
            db.tabSessionDao().deleteAll();
            List<TabSessionEntity> entities = new ArrayList<>();
            for (int i = 0; i < tabs.size(); i++) {
                BrowserTab tab = tabs.get(i);
                if (tab.isIncognito()) continue;
                TabSessionEntity entity = new TabSessionEntity();
                entity.id = tab.getId();
                entity.url = tab.getUrl();
                entity.title = tab.getTitle();
                entity.position = i;
                entity.isPinned = tab.isPinned();
                entity.groupId = tab.getGroupId();
                entity.savedAt = System.currentTimeMillis();
                entities.add(entity);
            }
            db.tabSessionDao().insertAll(entities);
        });
    }

    public void restoreSession(Context context) {
        executor.execute(() -> {
            List<TabSessionEntity> sessions = db.tabSessionDao().getAllTabs();
            List<String> urls = new ArrayList<>();
            for (TabSessionEntity s : sessions) {
                if (s.url != null && !s.url.isEmpty()) urls.add(s.url);
            }
            restoredTabs.postValue(urls);
        });
    }

    public void saveScreenshot(Bitmap bitmap, Context context) {
        executor.execute(() -> {
            try {
                File dir = new File(context.getExternalFilesDir(null), "Screenshots");
                dir.mkdirs();
                File file = new File(dir, "screenshot_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LiveData<List<String>> getRestoredTabs() { return restoredTabs; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
