package com.paperplanes.udas.domain.model;

import java.util.Date;

public class Session {

    private String mAccessToken;
    private Date mExpire;

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
}
