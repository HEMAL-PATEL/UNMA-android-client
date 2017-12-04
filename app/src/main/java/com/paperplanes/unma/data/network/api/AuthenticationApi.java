package com.paperplanes.unma.data.network.api;

import com.paperplanes.unma.data.network.api.response.JsonResp;
import com.paperplanes.unma.data.network.api.response.AuthRespData;

import io.reactivex.Single;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by abdularis on 31/10/17.
 */

public interface AuthenticationApi {

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
