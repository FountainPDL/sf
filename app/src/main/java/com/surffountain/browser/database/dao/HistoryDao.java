package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.HistoryEntity;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HistoryEntity history);

    @Update
    void update(HistoryEntity history);

    @Delete
    void delete(HistoryEntity history);

    @Query("SELECT * FROM history ORDER BY visited_at DESC")
    LiveData<List<HistoryEntity>> getAllHistory();

    @Query("SELECT * FROM history ORDER BY visited_at DESC LIMIT :limit")
    LiveData<List<HistoryEntity>> getRecentHistory(int limit);

    @Query("SELECT * FROM history WHERE url LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY visited_at DESC LIMIT 50")
    List<HistoryEntity> searchHistory(String query);

    @Query("SELECT * FROM history WHERE url LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY visit_count DESC LIMIT 5")
    List<HistoryEntity> searchHistorySuggestions(String query);

    @Query("SELECT * FROM history WHERE visited_at BETWEEN :start AND :end ORDER BY visited_at DESC")
    LiveData<List<HistoryEntity>> getHistoryBetween(long start, long end);

    @Query("DELETE FROM history WHERE visited_at < :before")
    void deleteOlderThan(long before);

    @Query("DELETE FROM history WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM history")
    void deleteAll();

    @Query("SELECT * FROM history WHERE url = :url ORDER BY visited_at DESC LIMIT 1")
    HistoryEntity getByUrl(String url);

    @Query("UPDATE history SET visit_count = visit_count + 1, visited_at = :time WHERE url = :url")
    void updateVisit(String url, long time);

    @Query("SELECT * FROM history ORDER BY visit_count DESC LIMIT :limit")
    List<HistoryEntity> getMostVisited(int limit);

    @Query("SELECT COUNT(*) FROM history")
    int getCount();

    @Query("SELECT * FROM history WHERE is_synced = 0")
    List<HistoryEntity> getUnsyncedHistory();

    @Query("UPDATE history SET is_synced = 1 WHERE id = :id")
    void markSynced(long id);
}
