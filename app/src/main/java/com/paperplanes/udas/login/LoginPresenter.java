package com.paperplanes.udas.login;

import com.paperplanes.udas.auth.Authentication;
import com.paperplanes.udas.common.NoConnectivityException;
import com.paperplanes.udas.model.LoginResult;

import java.net.SocketException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;


/**
 * Created by abdularis on 02/11/17.
 */

public class LoginPresenter {

    private LoginView mView;
    private Authentication mAuth;

    @Inject
    public LoginPresenter(Authentication auth) {
        mAuth = auth;
    }

    public void setView(LoginView view) {
        mView = view;
    }

    public void login(String username, String password) {
        if (username.isEmpty()) {
            mView.showUsernameError("Username tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            mView.showPasswordError("Password tidak boleh kosong");
            return;
        }

        mView.showLoading(true);
        mAuth.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mView.showLoading(false);
                        if (loginResult.isSuccess()) {
                            mView.onLoginSuccess();
                        }
                        else {
                            mView.onLoginFailed(loginResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.showLoading(false);
                        if (throwable instanceof NoConnectivityException) {
                            mView.showErrorMessage("Error: Tidak ada konektivitas.");
                        }
                        else if (throwable instanceof SocketException) {
                            mView.showErrorMessage("Error: Tidak dapat terhubung ke server.");
                        }
                        else {
                            mView.showErrorMessage("Error: Unknown error.");
                        }
                    }
                });

    }
}
