package com.surffountain.browser.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SyncService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Sync bookmarks, history, passwords across devices (stub)
        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
