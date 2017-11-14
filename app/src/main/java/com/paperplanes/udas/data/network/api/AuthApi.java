package com.paperplanes.udas.data.network.api;

import com.paperplanes.udas.data.network.api.response.JsonResp;
import com.paperplanes.udas.data.network.api.response.AuthRespData;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by abdularis on 31/10/17.
 */

public interface AuthApi {

    @FormUrlEncoded
    @POST("session")
    Single<JsonResp<AuthRespData>> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("fcm_token") String fcmToken
    );

    @DELETE("session")
    Single<JsonResp<Void>> logout();

}
