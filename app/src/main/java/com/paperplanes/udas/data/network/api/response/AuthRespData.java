package com.paperplanes.udas.data.network.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abdularis on 31/10/17.
 */

public class AuthRespData {

    @SerializedName("exp") long mExpire;
    @SerializedName("token") String mAccessToken;
    @SerializedName("name") String mName;
    @SerializedName("username") String mUsername;

    public long getExpire() {
        return mExpire;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getName() {
        return mName;
    }

    public String getUsername() {
        return mUsername;
    }
}
