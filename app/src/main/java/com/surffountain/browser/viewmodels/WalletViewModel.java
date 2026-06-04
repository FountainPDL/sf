package com.surffountain.browser.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.security.PasswordManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WalletViewModel extends ViewModel {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> address = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public WalletViewModel() {
        // Load wallet address from secure storage
    }

    public void connectWallet(Context context) {
        Toast.makeText(context,
            "Hardware wallet support via WalletConnect coming in v2.0.\nCreate a software wallet to get started.",
            Toast.LENGTH_LONG).show();
    }

    public void setRegion(String region) {}

    public void loadBalance(String walletAddress) {
        executor.execute(() -> {
            // Fetch ETH/SOL balance from RPC endpoint
            totalBalance.postValue(0.0);
        });
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<String> getAddress() { return address; }
    public LiveData<String> getError() { return error; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
