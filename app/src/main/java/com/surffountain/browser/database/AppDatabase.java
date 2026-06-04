package com.surffountain.browser.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.surffountain.browser.database.dao.BookmarkDao;
import com.surffountain.browser.database.dao.BookmarkFolderDao;
import com.surffountain.browser.database.dao.DownloadDao;
import com.surffountain.browser.database.dao.HistoryDao;
import com.surffountain.browser.database.dao.PasswordDao;
import com.surffountain.browser.database.dao.SpeedDialDao;
import com.surffountain.browser.database.dao.TabSessionDao;
import com.surffountain.browser.database.entities.BookmarkEntity;
import com.surffountain.browser.database.entities.BookmarkFolderEntity;
import com.surffountain.browser.database.entities.DownloadEntity;
import com.surffountain.browser.database.entities.HistoryEntity;
import com.surffountain.browser.database.entities.PasswordEntity;
import com.surffountain.browser.database.entities.SpeedDialEntity;
import com.surffountain.browser.database.entities.TabSessionEntity;

@Database(
    entities = {
        BookmarkEntity.class,
        BookmarkFolderEntity.class,
        HistoryEntity.class,
        DownloadEntity.class,
        PasswordEntity.class,
        TabSessionEntity.class,
        SpeedDialEntity.class
    },
    version = 1,
    exportSchema = true
)
@TypeConverters({DatabaseConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "surf_fountain.db";
    private static volatile AppDatabase instance;

    public abstract BookmarkDao bookmarkDao();
    public abstract BookmarkFolderDao bookmarkFolderDao();
    public abstract HistoryDao historyDao();
    public abstract DownloadDao downloadDao();
    public abstract PasswordDao passwordDao();
    public abstract TabSessionDao tabSessionDao();
    public abstract SpeedDialDao speedDialDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
