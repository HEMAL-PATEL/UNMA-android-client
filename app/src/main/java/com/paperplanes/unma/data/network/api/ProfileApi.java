package com.paperplanes.unma.data.network.api;

import com.paperplanes.unma.data.network.api.response.JsonResp;
import com.paperplanes.unma.data.network.api.response.ProfileRespData;
import com.paperplanes.unma.data.network.api.response.ProfileUpdateRespData;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by abdularis on 06/12/17.
 */

public interface ProfileApi {

    @GET("profile")
    Single<JsonResp<ProfileRespData>> getProfile();

    @POST("profile")
    @FormUrlEncoded
    Single<JsonResp<ProfileUpdateRespData>> updatePassword(@Field("old_password") String oldPassword,
                                                           @Field("new_password") String newPassword,
                                                           @Field("fcm_token") String fcmToken);

}
