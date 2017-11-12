package com.paperplanes.udas.domain.model;

public class LoginModel {

    private boolean mSuccess;
    private String mMessage;
    private String mAccessToken;
    private long mExpire;

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public long getExpire() {
        return mExpire;
    }

    public void setExpire(long expire) {
        mExpire = expire;
    }
}
