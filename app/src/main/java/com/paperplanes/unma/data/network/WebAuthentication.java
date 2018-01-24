package com.paperplanes.unma.data.network;


import com.google.firebase.iid.FirebaseInstanceId;
import com.paperplanes.unma.auth.Authentication;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.network.api.AuthenticationApi;
import com.paperplanes.unma.data.network.api.response.AuthRespData;
import com.paperplanes.unma.data.network.api.response.JsonResp;
import com.paperplanes.unma.model.LoginResult;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public abstract class WebAuthentication extends Authentication {

    private AuthenticationApi mAuthApi;

    public WebAuthentication(AuthenticationApi authApi, SessionManager sessionManager) {
        super(sessionManager);
        mAuthApi = authApi;
    }

    @Override
    protected Single<LoginResult> doLogin(String username, String password) {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        return mAuthApi.login(username, password, fcmToken, getUserType())
                .subscribeOn(Schedulers.io())
                .map(this::mapApiLoginResponse);
    }

    @Override
    protected Single<Boolean> doLogout() {
        return mAuthApi.logout()
                .subscribeOn(Schedulers.io())
                .map(resp -> resp != null && resp.isSuccess());
    }

    protected abstract int getUserType();

    private LoginResult mapApiLoginResponse(JsonResp<AuthRespData> resp) {
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
    }

}
