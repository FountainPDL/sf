package com.surffountain.browser.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.DownloadEntity;
import com.surffountain.browser.workers.DownloadWorker;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AppDatabase db = SurfFountainApp.getInstance().getDatabase();
    private LiveData<List<DownloadEntity>> downloads;

    public DownloadViewModel() {
        downloads = db.downloadDao().getAllDownloads();
    }

    public LiveData<List<DownloadEntity>> getDownloads() { return downloads; }

    public void loadAll() { downloads = db.downloadDao().getAllDownloads(); }
    public void loadActive() { downloads = db.downloadDao().getDownloadsByStatus("RUNNING"); }
    public void loadByCategory(String category) { downloads = db.downloadDao().getDownloadsByCategory(category); }

    public long enqueueDownload(String url, String filename, String mimeType, String referer) {
        DownloadEntity entity = new DownloadEntity();
        entity.url = url;
        entity.filename = filename;
        entity.mimeType = mimeType;
        entity.referer = referer;
        entity.category = com.surffountain.browser.models.DownloadItem.categoryFromMimeType(mimeType).name();

        long[] id = {0};
        executor.execute(() -> {
            id[0] = db.downloadDao().insert(entity);
            Data inputData = new Data.Builder()
                    .putLong("download_id", id[0])
                    .putString("url", url)
                    .putString("filename", filename)
                    .putString("mime_type", mimeType)
                    .build();
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                    .setInputData(inputData)
                    .addTag("download_" + id[0])
                    .build();
            String workId = workRequest.getId().toString();
            entity.workId = workId;
            db.downloadDao().update(entity);
            WorkManager.getInstance(SurfFountainApp.getAppContext()).enqueue(workRequest);
        });
        return id[0];
    }

    public void pauseOrResume(DownloadEntity item) {
        executor.execute(() -> {
            if ("RUNNING".equals(item.status)) {
                db.downloadDao().updateStatus(item.id, "PAUSED");
                if (item.workId != null) {
                    WorkManager.getInstance(SurfFountainApp.getAppContext())
                            .cancelWorkById(java.util.UUID.fromString(item.workId));
                }
            } else if ("PAUSED".equals(item.status)) {
                db.downloadDao().updateStatus(item.id, "RUNNING");
                // Re-enqueue
            }
        });
    }

    public void cancel(DownloadEntity item) {
        executor.execute(() -> {
            db.downloadDao().updateStatus(item.id, "CANCELLED");
            if (item.workId != null) {
                WorkManager.getInstance(SurfFountainApp.getAppContext())
                        .cancelWorkById(java.util.UUID.fromString(item.workId));
            }
        });
    }

    public void retry(DownloadEntity item) {
        enqueueDownload(item.url, item.filename, item.mimeType, item.referer);
    }

    public void openFile(DownloadEntity item, Context context) {
        if (item.filePath == null) return;
        File file = new File(item.filePath);
        if (!file.exists()) return;
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, item.mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Open with"));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
