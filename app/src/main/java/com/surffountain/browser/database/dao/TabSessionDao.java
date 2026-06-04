package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.TabSessionEntity;

import java.util.List;

@Dao
public interface TabSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TabSessionEntity tab);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TabSessionEntity> tabs);

    @Update
    void update(TabSessionEntity tab);

    @Delete
    void delete(TabSessionEntity tab);

    @Query("SELECT * FROM tab_sessions ORDER BY position ASC")
    List<TabSessionEntity> getAllTabs();

    @Query("SELECT * FROM tab_sessions ORDER BY position ASC")
    LiveData<List<TabSessionEntity>> getAllTabsLive();

    @Query("DELETE FROM tab_sessions")
    void deleteAll();

    @Query("DELETE FROM tab_sessions WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT COUNT(*) FROM tab_sessions")
    int getCount();

    @Query("SELECT * FROM tab_sessions WHERE group_id = :groupId ORDER BY position ASC")
    List<TabSessionEntity> getTabsInGroup(String groupId);
}
