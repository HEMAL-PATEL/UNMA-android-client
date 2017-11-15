package com.paperplanes.udas.auth;


import com.paperplanes.udas.model.LoginResult;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public abstract class Authentication {

    private SessionManager mSessionManager;

    public Authentication(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    public Single<LoginResult> login(String username, String password) {
        return doLogin(username, password)
                .doOnSuccess(new Consumer<LoginResult>() {
                    @Override
                    public void accept(LoginResult loginResult) throws Exception {
                        if (loginResult.isSuccess()) {
                            Session sess = new Session();
                            sess.setAccessToken(loginResult.getAccessToken());
                            sess.setExpire(new Date(new Timestamp(loginResult.getExpire()).getTime()));
                            sess.setName(loginResult.getName());
                            sess.setUsername(loginResult.getUsername());
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

    protected abstract Single<LoginResult> doLogin(String username, String password);

    protected abstract Single<Boolean> doLogout();

}
