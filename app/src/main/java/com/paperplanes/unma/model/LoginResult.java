package com.paperplanes.unma.model;

public class LoginResult {

    private boolean mSuccess;
    private String mMessage;
    private String mAccessToken;
    private String mName;
    private String mUsername;
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

    public long getExpire() {
        return mExpire;
    }

    public void setExpire(long expire) {
        mExpire = expire;
    }
}
