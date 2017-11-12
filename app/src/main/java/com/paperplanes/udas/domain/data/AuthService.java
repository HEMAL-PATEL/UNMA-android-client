package com.paperplanes.udas.domain.data;


import com.paperplanes.udas.domain.model.LoginModel;

import io.reactivex.Single;

public interface AuthService {

    Single<LoginModel> login(String username, String password);

    Single<Boolean> logout(String accToken);

}
