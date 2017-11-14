package com.paperplanes.udas.main;

import com.paperplanes.udas.auth.Authentication;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by abdularis on 14/11/17.
 */

public class MainPresenter {

    private MainView mView;
    private Authentication mAuth;

    @Inject
    public MainPresenter(Authentication auth) {
        mAuth = auth;
    }

    public void setView(MainView view) {
        mView = view;
    }

    public void logout() {
        mAuth.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mView.onLogoutSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.onLogoutFailed("Failed");
                    }
                });
    }

}
