package com.paperplanes.udas.data;


import com.google.firebase.iid.FirebaseInstanceId;
import com.paperplanes.udas.data.api.AuthApi;
import com.paperplanes.udas.data.api.ServiceGenerator;
import com.paperplanes.udas.data.api.response.AuthRespData;
import com.paperplanes.udas.data.api.response.JsonResp;
import com.paperplanes.udas.domain.data.AuthService;
import com.paperplanes.udas.domain.model.LoginModel;

import io.reactivex.Single;
import io.reactivex.functions.Function;

public class NetAuthService implements AuthService {

    private AuthApi mAuthApi;

    public NetAuthService(AuthApi authApi) {
        mAuthApi = authApi;
    }

    @Override
    public Single<LoginModel> login(String username, String password) {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        return mAuthApi.login(username, password, fcmToken)
                .map(resp -> {
                    LoginModel model = new LoginModel();
                    model.setSuccess(resp.isSuccess());
                    model.setMessage(resp.getMessage());
                    if (resp.isSuccess() && resp.getData() != null) {
                        model.setAccessToken(resp.getData().getAccessToken());
                        model.setExpire(resp.getData().getExpire());
                    }
                    return model;
                });
    }

    @Override
    public Single<Boolean> logout(String accToken) {
        return mAuthApi.logout("key=" + accToken)
                .map(resp -> resp != null && resp.isSuccess());
    }

}
