package com.paperplanes.udas.auth;

import java.util.Date;

public class Session {

    private String mAccessToken;
    private Date mExpire;
    private String mName;
    private String mUsername;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public Date getExpire() {
        return mExpire;
    }

    public void setExpire(Date expire) {
        mExpire = expire;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}
