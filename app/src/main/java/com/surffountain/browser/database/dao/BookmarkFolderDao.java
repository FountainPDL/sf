package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.BookmarkFolderEntity;

import java.util.List;

@Dao
public interface BookmarkFolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BookmarkFolderEntity folder);

    @Update
    void update(BookmarkFolderEntity folder);

    @Delete
    void delete(BookmarkFolderEntity folder);

    @Query("SELECT * FROM bookmark_folders WHERE parent_id IS NULL ORDER BY sort_order ASC, name ASC")
    LiveData<List<BookmarkFolderEntity>> getRootFolders();

    @Query("SELECT * FROM bookmark_folders WHERE parent_id = :parentId ORDER BY sort_order ASC, name ASC")
    LiveData<List<BookmarkFolderEntity>> getSubFolders(long parentId);

    @Query("SELECT * FROM bookmark_folders WHERE id = :id")
    BookmarkFolderEntity getById(long id);

    @Query("SELECT * FROM bookmark_folders ORDER BY name ASC")
    List<BookmarkFolderEntity> getAllFolders();

    @Query("DELETE FROM bookmark_folders WHERE id = :id")
    void deleteById(long id);
}
