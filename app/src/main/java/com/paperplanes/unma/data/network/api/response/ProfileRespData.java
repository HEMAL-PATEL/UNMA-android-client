package com.paperplanes.unma.data.network.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abdularis on 06/12/17.
 */

public class ProfileRespData {

    @SerializedName("type") int mUserType;
    @SerializedName("name") String mName;
    @SerializedName("username") String mUsername;
    @SerializedName("fcm_token") String mFcmToken;
    @SerializedName("class") ClassRespData mClass;

    public int getUserType() { return mUserType; }

    public String getName() {
        return mName;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getFcmToken() {
        return mFcmToken;
    }

    public ClassRespData getRespClass() {
        return mClass;
    }

    public static class ClassRespData {
        String prog;
        String name;
        int year;
        String type;

        public String getProg() {
            return prog;
        }

        public String getName() {
            return name;
        }

        public int getYear() {
            return year;
        }

        public String getType() {
            return type;
        }
    }

}
