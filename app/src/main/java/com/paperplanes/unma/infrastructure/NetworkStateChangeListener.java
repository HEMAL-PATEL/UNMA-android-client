package com.paperplanes.unma.infrastructure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.paperplanes.unma.common.NetworkUtil;
import com.paperplanes.unma.data.AnnouncementRepository;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 04/12/17.
 */

public class NetworkStateChangeListener extends BroadcastReceiver {
    private static final String TAG = NetworkStateChangeListener.class.getSimpleName();

    private AnnouncementRepository mRepository;

    public NetworkStateChangeListener(AnnouncementRepository repository) {
        mRepository = repository;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkUtil.isOnline(context)) {
                Log.d(TAG, "Network state change : goes online");
                mRepository.processAllPendingTask()
                        .subscribeOn(Schedulers.io())
                        .subscribe(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Log.d(TAG, "Pending task successfully completed!");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.d(TAG, "Pending task failed to complete: " + throwable.toString());
                            }
                        });
            }
            else {
                Log.d(TAG, "Network state change : goes offline");
            }
        }
    }
}
