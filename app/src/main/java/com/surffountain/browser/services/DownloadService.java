package com.surffountain.browser.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.surffountain.browser.R;

public class DownloadService extends Service {

    private static final String CHANNEL_ID = "download_service_channel";
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DownloadService getService() { return DownloadService.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(3001,
            new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Surf Fountain")
                .setContentText("Downloading...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return binder; }

    private void createChannel() {
        NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
    }
}
