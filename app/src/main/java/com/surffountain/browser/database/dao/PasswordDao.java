package com.surffountain.browser.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.surffountain.browser.database.entities.PasswordEntity;

import java.util.List;

@Dao
public interface PasswordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PasswordEntity password);

    @Update
    void update(PasswordEntity password);

    @Delete
    void delete(PasswordEntity password);

    @Query("SELECT * FROM passwords ORDER BY domain ASC")
    LiveData<List<PasswordEntity>> getAllPasswords();

    @Query("SELECT * FROM passwords WHERE domain LIKE '%' || :domain || '%' ORDER BY last_used_at DESC")
    List<PasswordEntity> getForDomain(String domain);

    @Query("SELECT * FROM passwords WHERE domain = :domain AND username = :username LIMIT 1")
    PasswordEntity getByDomainAndUsername(String domain, String username);

    @Query("SELECT * FROM passwords WHERE domain LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    LiveData<List<PasswordEntity>> search(String query);

    @Query("DELETE FROM passwords WHERE id = :id")
    void deleteById(long id);

    @Query("UPDATE passwords SET use_count = use_count + 1, last_used_at = :time WHERE id = :id")
    void recordUse(long id, long time);

    @Query("UPDATE passwords SET is_compromised = :compromised WHERE id = :id")
    void setCompromised(long id, boolean compromised);

    @Query("SELECT COUNT(*) FROM passwords")
    int getCount();

    @Query("SELECT * FROM passwords WHERE is_synced = 0")
    List<PasswordEntity> getUnsyncedPasswords();

    @Query("UPDATE passwords SET is_synced = 1 WHERE id = :id")
    void markSynced(long id);

    @Query("SELECT * FROM passwords WHERE is_compromised = 1")
    LiveData<List<PasswordEntity>> getCompromisedPasswords();

    @Query("DELETE FROM passwords")
    void deleteAll();
}
