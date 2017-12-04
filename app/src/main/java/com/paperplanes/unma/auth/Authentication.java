package com.paperplanes.unma.auth;


import com.paperplanes.unma.model.LoginResult;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.Single;

public abstract class Authentication {

    private SessionManager mSessionManager;

    public Authentication(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    public Single<LoginResult> login(String username, String password) {
        return doLogin(username, password)
                .doOnSuccess(loginResult -> {
                    if (loginResult.isSuccess()) {
                        Session sess = new Session();
                        sess.setAccessToken(loginResult.getAccessToken());
                        sess.setExpire(new Date(new Timestamp(loginResult.getExpire()).getTime()));
                        sess.setName(loginResult.getName());
                        sess.setUsername(loginResult.getUsername());
                        mSessionManager.setSession(sess);
                    }
                });
    }

    public Single<Boolean> logout() {
        return doLogout()
                .doOnSuccess(success -> {
                    if (success) {
                        mSessionManager.clearSession(SessionManager.LogoutEvent.Cause.Intentional);
                    }
                });
    }

    protected abstract Single<LoginResult> doLogin(String username, String password);

    protected abstract Single<Boolean> doLogout();

}
