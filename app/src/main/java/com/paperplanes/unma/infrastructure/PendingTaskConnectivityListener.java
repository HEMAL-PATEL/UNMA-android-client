package com.paperplanes.unma.infrastructure;

import android.util.Log;

import com.paperplanes.unma.data.AnnouncementRepository;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 19/12/17.
 */

public class PendingTaskConnectivityListener
        implements DeviceConnectivityObserver.ConnectivityStateChangeListener {
    private static final String TAG = PendingTaskConnectivityListener.class.getSimpleName();

    private AnnouncementRepository mRepository;

    public PendingTaskConnectivityListener(AnnouncementRepository repository) {
        mRepository = repository;
    }

    @Override
    public void onDeviceOnline() {
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

    @Override
    public void onDeviceOffline() {}
}
