package com.paperplanes.udas.common;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.paperplanes.udas.auth.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abdularis on 14/11/17.
 */

public class AuthInterceptor implements Interceptor {

    private static final int HTTP_UNAUTHORIZED_RESPONSE_CODE = 401;

    private Context mContext;
    private SessionManager mSessionManager;

    public AuthInterceptor(Context context, SessionManager sessionManager) {
        mSessionManager = sessionManager;
        mContext = context;
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
            mSessionManager.clearSession();

            Intent intent = new Intent("UnauthorizedAccess");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        return response;
    }
}