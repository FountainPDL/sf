package com.surffountain.browser.viewmodels;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surffountain.browser.services.SurfVpnService;

public class VpnViewModel extends ViewModel {

    private final MutableLiveData<Boolean> connected = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private String selectedRegion = "Auto";
    private boolean killSwitchEnabled = false;
    private boolean autoConnectEnabled = false;

    public void startVpn(Context context) {
        Intent intent = new Intent(context, SurfVpnService.class);
        intent.putExtra("region", selectedRegion);
        intent.putExtra("kill_switch", killSwitchEnabled);
        context.startService(intent);
        connected.postValue(true);
    }

    public void stopVpn(Context context) {
        Intent intent = new Intent(context, SurfVpnService.class);
        intent.setAction("STOP");
        context.startService(intent);
        connected.postValue(false);
    }

    public void setRegion(String region) { this.selectedRegion = region; }
    public void setKillSwitch(boolean v) { this.killSwitchEnabled = v; }
    public void setAutoConnect(boolean v) { this.autoConnectEnabled = v; }

    public LiveData<Boolean> isConnected() { return connected; }
    public LiveData<String> getError() { return error; }
}
