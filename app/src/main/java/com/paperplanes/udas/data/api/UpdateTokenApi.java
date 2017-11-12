package com.paperplanes.udas.data.api;

import com.paperplanes.udas.data.api.response.JsonResp;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by abdularis on 01/11/17.
 */

public interface UpdateTokenApi {

    @FormUrlEncoded
    @POST("token")
    Single<JsonResp<Void>> updateTokenOnServer(
            @Header("Authorization") String accToken,
            @Field("fcm_token") String fcmToken);

}
