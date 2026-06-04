package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.DownloadEntity;

import java.util.List;

@Dao
public interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DownloadEntity download);

    @Update
    void update(DownloadEntity download);

    @Delete
    void delete(DownloadEntity download);

    @Query("SELECT * FROM downloads ORDER BY started_at DESC")
    LiveData<List<DownloadEntity>> getAllDownloads();

    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY started_at DESC")
    LiveData<List<DownloadEntity>> getDownloadsByStatus(String status);

    @Query("SELECT * FROM downloads WHERE category = :category ORDER BY started_at DESC")
    LiveData<List<DownloadEntity>> getDownloadsByCategory(String category);

    @Query("SELECT * FROM downloads WHERE status IN ('RUNNING', 'PENDING', 'PAUSED') ORDER BY started_at ASC")
    LiveData<List<DownloadEntity>> getActiveDownloads();

    @Query("SELECT * FROM downloads WHERE id = :id")
    DownloadEntity getById(long id);

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    void updateStatus(long id, String status);

    @Query("UPDATE downloads SET downloaded_bytes = :downloaded, status = :status WHERE id = :id")
    void updateProgress(long id, long downloaded, String status);

    @Query("DELETE FROM downloads WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM downloads WHERE status = 'COMPLETED'")
    void deleteCompleted();

    @Query("SELECT * FROM downloads WHERE filename LIKE '%' || :query || '%'")
    List<DownloadEntity> searchDownloads(String query);
}
