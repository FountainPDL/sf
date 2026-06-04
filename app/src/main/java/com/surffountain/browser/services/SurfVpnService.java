package com.surffountain.browser.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.surffountain.browser.R;

import java.io.IOException;

public class SurfVpnService extends VpnService {

    private static final String TAG = "SurfVpnService";
    private static final String CHANNEL_ID = "vpn_channel";
    private static final int NOTIFICATION_ID = 2001;

    private ParcelFileDescriptor vpnInterface;
    private Thread vpnThread;
    private boolean running = false;
    private String selectedRegion = "Auto";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopVpn();
            return START_NOT_STICKY;
        }

        if (intent != null) {
            selectedRegion = intent.getStringExtra("region") != null ?
                    intent.getStringExtra("region") : "Auto";
        }

        startForeground(NOTIFICATION_ID, buildNotification());
        startVpn();
        return START_STICKY;
    }

    private void startVpn() {
        try {
            Builder builder = new Builder()
                    .setSession("Surf VPN")
                    .addAddress("10.0.0.2", 32)
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer("1.1.1.1")
                    .addDnsServer("9.9.9.9");

            vpnInterface = builder.establish();
            if (vpnInterface == null) {
                Log.e(TAG, "Failed to establish VPN interface");
                return;
            }

            running = true;
            vpnThread = new Thread(this::runVpnLoop, "SurfVpnThread");
            vpnThread.start();

            Log.d(TAG, "VPN started, region: " + selectedRegion);
        } catch (Exception e) {
            Log.e(TAG, "VPN start error", e);
        }
    }

    private void runVpnLoop() {
        // In a real implementation, this would tunnel traffic through
        // a proxy/VPN server. This is the stub that keeps the service alive.
        while (running && !Thread.interrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void stopVpn() {
        running = false;
        if (vpnThread != null) vpnThread.interrupt();
        if (vpnInterface != null) {
            try { vpnInterface.close(); } catch (IOException e) { Log.e(TAG, "Close error", e); }
        }
        stopForeground(true);
        stopSelf();
    }

    private android.app.Notification buildNotification() {
        createChannel();
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Surf VPN Active")
                .setContentText("Region: " + selectedRegion)
                .setSmallIcon(R.drawable.ic_vpn)
                .setOngoing(true)
                .build();
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "VPN", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        stopVpn();
        super.onDestroy();
    }
}
