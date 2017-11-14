package com.paperplanes.udas.auth;


import com.paperplanes.udas.model.LoginModel;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public abstract class Authentication {

    private SessionManager mSessionManager;

    public Authentication(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    public Single<LoginModel> login(String username, String password) {
        return doLogin(username, password)
                .doOnSuccess(new Consumer<LoginModel>() {
                    @Override
                    public void accept(LoginModel loginModel) throws Exception {
                        if (loginModel.isSuccess()) {
                            Session sess = new Session();
                            sess.setAccessToken(loginModel.getAccessToken());
                            sess.setExpire(new Date(new Timestamp(loginModel.getExpire()).getTime()));
                            sess.setName(loginModel.getName());
                            sess.setUsername(loginModel.getUsername());
                            mSessionManager.setSession(sess);
                        }
                    }
                });
    }

    public Single<Boolean> logout() {
        return doLogout()
                .doOnSuccess(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            mSessionManager.clearSession();
                        }
                    }
                });
    }

    protected abstract Single<LoginModel> doLogin(String username, String password);

    protected abstract Single<Boolean> doLogout();

}
