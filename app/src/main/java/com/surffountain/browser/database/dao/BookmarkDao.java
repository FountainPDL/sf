package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.BookmarkEntity;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BookmarkEntity bookmark);

    @Update
    void update(BookmarkEntity bookmark);

    @Delete
    void delete(BookmarkEntity bookmark);

    @Query("DELETE FROM bookmarks WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM bookmarks WHERE folder_id IS NULL ORDER BY sort_order ASC, created_at DESC")
    LiveData<List<BookmarkEntity>> getRootBookmarks();

    @Query("SELECT * FROM bookmarks WHERE folder_id = :folderId ORDER BY sort_order ASC, created_at DESC")
    LiveData<List<BookmarkEntity>> getBookmarksInFolder(long folderId);

    @Query("SELECT * FROM bookmarks WHERE url LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY visit_count DESC LIMIT 20")
    List<BookmarkEntity> searchBookmarks(String query);

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    BookmarkEntity getByUrl(String url);

    @Query("SELECT * FROM bookmarks ORDER BY visit_count DESC LIMIT :limit")
    LiveData<List<BookmarkEntity>> getMostVisited(int limit);

    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC LIMIT :limit")
    LiveData<List<BookmarkEntity>> getRecentBookmarks(int limit);

    @Query("UPDATE bookmarks SET visit_count = visit_count + 1 WHERE id = :id")
    void incrementVisitCount(long id);

    @Query("SELECT COUNT(*) FROM bookmarks")
    int getCount();

    @Query("SELECT * FROM bookmarks WHERE is_synced = 0")
    List<BookmarkEntity> getUnsyncedBookmarks();

    @Query("UPDATE bookmarks SET is_synced = 1 WHERE id = :id")
    void markSynced(long id);

    @Query("DELETE FROM bookmarks")
    void deleteAll();

    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    List<BookmarkEntity> getAllForExport();
}
