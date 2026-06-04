package com.surffountain.browser.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.surffountain.browser.SurfFountainApp;
import com.surffountain.browser.settings.SettingsManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SettingsManager sm = SettingsManager.getInstance(context);
            // Re-schedule downloads, auto-connect VPN if enabled
            if (sm.isAdBlockEnabled()) {
                // Re-initialize adblock lists
                com.surffountain.browser.privacy.AdBlocker blocker =
                        com.surffountain.browser.privacy.AdBlocker.getInstance(context);
                new Thread(blocker::initialize).start();
            }
        }
    }
}
