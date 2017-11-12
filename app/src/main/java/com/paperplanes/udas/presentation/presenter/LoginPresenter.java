package com.paperplanes.udas.presentation.presenter;

import com.paperplanes.udas.domain.model.LoginModel;
import com.paperplanes.udas.domain.usecase.LoginInteractor;
import com.paperplanes.udas.presentation.view.LoginView;

import java.net.ConnectException;

import javax.inject.Inject;

import com.paperplanes.udas.domain.usecase.DefaultObserver;


/**
 * Created by abdularis on 02/11/17.
 */

public class LoginPresenter {

    private LoginView mView;

    @Inject
    LoginInteractor mLoginInteractor;

    @Inject
    public LoginPresenter() {
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

        LoginInteractor.Params loginParams = new LoginInteractor.Params();
        loginParams.username = username;
        loginParams.password = password;

        mView.showLoading(true);
        mLoginInteractor.exec(loginParams)
                .subscribe(new DefaultObserver<LoginModel>() {
                    @Override
                    public void onNext(LoginModel loginModel) {
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
                        if (throwable instanceof ConnectException) {
                            mView.showErrorMessage("Tidak dapat terhubung ke server!");
                        }
                        else {
                            mView.showErrorMessage(throwable.getMessage());
                        }
                    }
                });

    }
}
