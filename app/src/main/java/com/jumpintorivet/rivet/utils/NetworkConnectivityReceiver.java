package com.jumpintorivet.rivet.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jumpintorivet.rivet.application.MyApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    @Inject
    Bus bus;
    private boolean hasInjected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!hasInjected) {
            hasInjected = true;
            ((MyApplication) context.getApplicationContext()).inject(this);
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (((wifiNetworkInfo != null && wifiNetworkInfo.isAvailable())
                || (mobileNetworkInfo != null && mobileNetworkInfo.isAvailable()))
                && activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting()) {
            bus.post(new InternetConnectionAvailableEvent());
        }
    }

    public static class InternetConnectionAvailableEvent {
    }
}
