package com.surffountain.browser.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.SpeedDialEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AppDatabase db = SurfFountainApp.getInstance().getDatabase();
    private final LiveData<List<SpeedDialEntity>> speedDials;

    public HomeViewModel() {
        speedDials = db.speedDialDao().getAll();
    }

    public LiveData<List<SpeedDialEntity>> getSpeedDials() { return speedDials; }

    public void addSpeedDial(String url, String title) {
        executor.execute(() -> {
            SpeedDialEntity entity = new SpeedDialEntity();
            entity.url = url;
            entity.title = title;
            entity.position = db.speedDialDao().getMaxPosition() + 1;
            entity.faviconUrl = com.surffountain.browser.utils.UrlUtils.getFaviconUrl(url);
            db.speedDialDao().insert(entity);
        });
    }

    public void deleteSpeedDial(SpeedDialEntity item) {
        executor.execute(() -> db.speedDialDao().delete(item));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
