package com.paperplanes.unma.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.R;
import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Authentication;
import com.paperplanes.unma.common.exceptions.NoConnectivityException;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.data.AnnouncementRepository;

import java.net.SocketException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by abdularis on 16/11/17.
 */

public class MainViewModel extends ViewModel {

    private Authentication mAuthentication;
    private AnnouncementRepository mRepository;
    private ResourceProvider mResourceProvider;

    private SingleLiveEvent<Boolean> mLogoutResult;
    private SingleLiveEvent<Throwable> mLogoutErr;

    @Inject
    public MainViewModel(ResourceProvider resourceProvider,
                         Authentication authentication, AnnouncementRepository repository) {
        mAuthentication = authentication;
        mRepository = repository;
        mResourceProvider = resourceProvider;

        mLogoutResult = new SingleLiveEvent<>();
        mLogoutErr = new SingleLiveEvent<>();
    }

    void logout() {
        mAuthentication.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        if (success) {
                            mRepository.clearAll();
                        }
                        mLogoutResult.setValue(success);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLogoutErr.setValue(throwable);
                    }
                });
    }

    MutableLiveData<Boolean> getLogoutResult() {
        return mLogoutResult;
    }

    MutableLiveData<Throwable> getLogoutErr() {
        return mLogoutErr;
    }
}
