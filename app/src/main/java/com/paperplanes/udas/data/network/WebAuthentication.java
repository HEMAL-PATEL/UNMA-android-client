package com.paperplanes.udas.data.network;


import com.google.firebase.iid.FirebaseInstanceId;
import com.paperplanes.udas.auth.Authentication;
import com.paperplanes.udas.auth.SessionManager;
import com.paperplanes.udas.data.network.api.AuthApi;
import com.paperplanes.udas.model.LoginResult;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class WebAuthentication extends Authentication {

    private AuthApi mAuthApi;

    public WebAuthentication(AuthApi authApi, SessionManager sessionManager) {
        super(sessionManager);
        mAuthApi = authApi;
    }

    @Override
    protected Single<LoginResult> doLogin(String username, String password) {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        return mAuthApi.login(username, password, fcmToken)
                .subscribeOn(Schedulers.io())
                .map(resp -> {
                    LoginResult model = new LoginResult();
                    model.setSuccess(resp.isSuccess());
                    model.setMessage(resp.getMessage());
                    if (resp.isSuccess() && resp.getData() != null) {
                        model.setAccessToken(resp.getData().getAccessToken());
                        model.setExpire(resp.getData().getExpire());
                        model.setName(resp.getData().getName());
                        model.setUsername(resp.getData().getUsername());
                    }
                    return model;
                });
    }

    @Override
    protected Single<Boolean> doLogout() {
        return mAuthApi.logout()
                .subscribeOn(Schedulers.io())
                .map(resp -> resp != null && resp.isSuccess());
    }

}
