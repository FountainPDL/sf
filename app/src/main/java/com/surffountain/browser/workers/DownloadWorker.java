package com.surffountain.browser.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.surffountain.browser.R;
import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker extends Worker {

    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final int BUFFER_SIZE = 8192;

    private final AppDatabase db;
    private final NotificationManager notificationManager;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        db = SurfFountainApp.getInstance().getDatabase();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @NonNull
    @Override
    public Result doWork() {
        long downloadId = getInputData().getLong("download_id", -1);
        String urlStr = getInputData().getString("url");
        String filename = getInputData().getString("filename");
        String mimeType = getInputData().getString("mime_type");

        if (downloadId == -1 || urlStr == null || filename == null) {
            return Result.failure();
        }

        try {
            setForegroundAsync(createForegroundInfo(filename, 0));
            db.downloadDao().updateStatus(downloadId, "RUNNING");

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            long totalBytes = connection.getContentLengthLong();

            File downloadDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir == null) downloadDir = getApplicationContext().getFilesDir();
            downloadDir.mkdirs();

            File outputFile = new File(downloadDir, filename);
            if (outputFile.exists()) {
                String base = filename.contains(".") ?
                        filename.substring(0, filename.lastIndexOf('.')) : filename;
                String ext = filename.contains(".") ?
                        filename.substring(filename.lastIndexOf('.')) : "";
                outputFile = new File(downloadDir, base + "_" + System.currentTimeMillis() + ext);
            }

            String finalPath = outputFile.getAbsolutePath();

            try (InputStream is = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                long downloaded = 0;
                int read;
                long lastUpdate = System.currentTimeMillis();

                while ((read = is.read(buffer)) != -1) {
                    if (isStopped()) {
                        db.downloadDao().updateStatus(downloadId, "PAUSED");
                        return Result.success();
                    }
                    fos.write(buffer, 0, read);
                    downloaded += read;

                    long now = System.currentTimeMillis();
                    if (now - lastUpdate > 500) {
                        int progress = totalBytes > 0 ? (int) ((downloaded * 100) / totalBytes) : 0;
                        db.downloadDao().updateProgress(downloadId, downloaded, "RUNNING");
                        setForegroundAsync(createForegroundInfo(filename, progress));
                        lastUpdate = now;
                    }
                }
            }

            db.downloadDao().updateStatus(downloadId, "COMPLETED");
            showCompletionNotification(filename, finalPath);
            return Result.success();

        } catch (Exception e) {
            db.downloadDao().updateStatus(downloadId, "FAILED");
            return Result.failure();
        }
    }

    private ForegroundInfo createForegroundInfo(String filename, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Downloading: " + filename)
                .setContentText(progress + "%")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setProgress(100, progress, progress == 0)
                .setOngoing(true)
                .setOnlyAlertOnce(true);
        return new ForegroundInfo(NOTIFICATION_ID, builder.build());
    }

    private void showCompletionNotification(String filename, String path) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Download complete")
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setAutoCancel(true);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Browser download progress");
        notificationManager.createNotificationChannel(channel);
    }
}
