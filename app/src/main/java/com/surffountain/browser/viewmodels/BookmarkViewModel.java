package com.surffountain.browser.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.BookmarkEntity;
import com.surffountain.browser.database.entities.BookmarkFolderEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AppDatabase db = SurfFountainApp.getInstance().getDatabase();
    private final MutableLiveData<List<BookmarkEntity>> searchResults = new MutableLiveData<>();
    private LiveData<List<BookmarkEntity>> bookmarks;
    private Long currentFolderId = null;

    public BookmarkViewModel() {
        bookmarks = db.bookmarkDao().getRootBookmarks();
    }

    public LiveData<List<BookmarkEntity>> getBookmarks() {
        return bookmarks;
    }

    public void navigateToFolder(Long folderId) {
        currentFolderId = folderId;
        if (folderId == null) {
            bookmarks = db.bookmarkDao().getRootBookmarks();
        } else {
            bookmarks = db.bookmarkDao().getBookmarksInFolder(folderId);
        }
    }

    public void search(String query) {
        if (query.isEmpty()) {
            bookmarks = currentFolderId == null ?
                    db.bookmarkDao().getRootBookmarks() :
                    db.bookmarkDao().getBookmarksInFolder(currentFolderId);
            return;
        }
        executor.execute(() -> {
            List<BookmarkEntity> results = db.bookmarkDao().searchBookmarks(query);
            searchResults.postValue(results);
        });
    }

    public void update(BookmarkEntity bookmark) {
        executor.execute(() -> {
            bookmark.modifiedAt = System.currentTimeMillis();
            db.bookmarkDao().update(bookmark);
        });
    }

    public void delete(BookmarkEntity bookmark) {
        executor.execute(() -> db.bookmarkDao().delete(bookmark));
    }

    public void createFolder(String name, Long parentId) {
        executor.execute(() -> {
            BookmarkFolderEntity folder = new BookmarkFolderEntity();
            folder.name = name;
            folder.parentId = parentId;
            db.bookmarkFolderDao().insert(folder);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
