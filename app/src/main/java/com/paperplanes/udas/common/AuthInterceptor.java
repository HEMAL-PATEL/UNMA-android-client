package com.paperplanes.udas.common;

import android.support.annotation.NonNull;

import com.paperplanes.udas.auth.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abdularis on 14/11/17.
 */

public class AuthInterceptor implements Interceptor {

    private SessionManager mSessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!mSessionManager.isSessionSet()) {
            return chain.proceed(chain.request());
        }

        String accessToken = mSessionManager.getSession().getAccessToken();
        Request origReq = chain.request();
        Request.Builder requestBuilder = origReq.newBuilder()
                .header("Authorization", "key=" + accessToken);

        return chain.proceed(requestBuilder.build());
    }
}