package com.paperplanes.udas.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abdularis on 31/10/17.
 */

public class AuthRespData {

    @SerializedName("exp") long mExpire;
    @SerializedName("token") String mAccessToken;

    public long getExpire() {
        return mExpire;
    }

    public String getAccessToken() {
        return mAccessToken;
    }
}
