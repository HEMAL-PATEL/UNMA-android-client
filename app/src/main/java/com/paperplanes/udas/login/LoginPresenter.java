package com.paperplanes.udas.login;

import com.paperplanes.udas.auth.Authentication;
import com.paperplanes.udas.auth.SessionManager;
import com.paperplanes.udas.common.NoConnectivityException;
import com.paperplanes.udas.model.LoginModel;

import java.net.ConnectException;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

import com.paperplanes.udas.auth.Session;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
                .subscribeWith(new DisposableSingleObserver<LoginModel>() {
                    @Override
                    public void onSuccess(LoginModel loginModel) {
                        mView.showLoading(false);
                        if (loginModel.isSuccess()) {
                            mView.onLoginSuccess();
                        }
                        else {
                            mView.onLoginFailed(loginModel.getMessage());
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
