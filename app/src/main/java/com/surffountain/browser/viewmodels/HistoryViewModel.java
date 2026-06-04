package com.surffountain.browser.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.database.AppDatabase;
import com.surffountain.browser.database.entities.HistoryEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AppDatabase db = SurfFountainApp.getInstance().getDatabase();
    private LiveData<List<HistoryEntity>> history;

    public HistoryViewModel() {
        history = db.historyDao().getRecentHistory(500);
    }

    public LiveData<List<HistoryEntity>> getHistory() { return history; }

    public void search(String query) {
        // For search, use executor + postValue pattern
        executor.execute(() -> {
            List<HistoryEntity> results = db.historyDao().searchHistory(query);
            // Post results through a separate LiveData if needed
        });
    }

    public void delete(HistoryEntity item) {
        executor.execute(() -> db.historyDao().delete(item));
    }

    public void clearAll() {
        executor.execute(() -> db.historyDao().deleteAll());
    }

    public void clearOlderThan(long timestamp) {
        executor.execute(() -> db.historyDao().deleteOlderThan(timestamp));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
