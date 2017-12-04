package com.paperplanes.unma.common;

import android.content.Context;
import android.support.annotation.NonNull;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.exceptions.UnauthorizedNetworkException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abdularis on 14/11/17.
 */

public class AuthorizationInterceptor implements Interceptor {

    private static final int HTTP_UNAUTHORIZED_RESPONSE_CODE = 401;

    private SessionManager mSessionManager;

    public AuthorizationInterceptor(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (mSessionManager.isSessionSet()) {
            String accessToken = mSessionManager.getSession().getAccessToken();
            Request.Builder requestBuilder = request.newBuilder()
                    .header("Authorization", "key=" + accessToken);
            request = requestBuilder.build();
        }

        Response response = chain.proceed(request);
        if (response.code() == HTTP_UNAUTHORIZED_RESPONSE_CODE) {
            mSessionManager.clearSession(SessionManager.LogoutEvent.Cause.UnIntentional);
            throw new UnauthorizedNetworkException();
        }

        return response;
    }
}