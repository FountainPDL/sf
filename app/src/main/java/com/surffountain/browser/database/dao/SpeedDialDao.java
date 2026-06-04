package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.SpeedDialEntity;

import java.util.List;

@Dao
public interface SpeedDialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SpeedDialEntity speedDial);

    @Update
    void update(SpeedDialEntity speedDial);

    @Delete
    void delete(SpeedDialEntity speedDial);

    @Query("SELECT * FROM speed_dial ORDER BY position ASC")
    LiveData<List<SpeedDialEntity>> getAll();

    @Query("SELECT * FROM speed_dial ORDER BY position ASC")
    List<SpeedDialEntity> getAllSync();

    @Query("DELETE FROM speed_dial WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT MAX(position) FROM speed_dial")
    int getMaxPosition();

    @Query("SELECT COUNT(*) FROM speed_dial")
    int getCount();
}
