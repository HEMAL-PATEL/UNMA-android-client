package com.paperplanes.unma.infrastructure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.paperplanes.unma.common.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 04/12/17.
 */

public class DeviceConnectivityObserver extends BroadcastReceiver {
    private static final String TAG = DeviceConnectivityObserver.class.getSimpleName();

    private List<ConnectivityStateChangeListener> mListenerList;

    public DeviceConnectivityObserver() {
        mListenerList = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkUtil.isOnline(context)) {
                Log.d(TAG, "Network state change : goes online");
                callOnOnlineListener();
            }
            else {
                Log.d(TAG, "Network state change : goes offline");
                callOnOfflineListener();
            }
        }
    }

    public void addConnectivityStateChangeListener(ConnectivityStateChangeListener listener) {
        if (!mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    private void removeConnectivityStateChangeListener(ConnectivityStateChangeListener listener) {
        mListenerList.remove(listener);
    }

    private void callOnOnlineListener() {
        for (ConnectivityStateChangeListener listener : mListenerList) listener.onDeviceOnline();
    }

    private void callOnOfflineListener() {
        for (ConnectivityStateChangeListener listener : mListenerList) listener.onDeviceOffline();
    }

    public interface ConnectivityStateChangeListener {
        void onDeviceOnline();
        void onDeviceOffline();
    }
}
