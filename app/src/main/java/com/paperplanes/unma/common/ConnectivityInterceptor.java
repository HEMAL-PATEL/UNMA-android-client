package com.paperplanes.unma.common;

import android.content.Context;
import android.support.annotation.NonNull;

import com.paperplanes.unma.common.exceptions.NoConnectivityException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by abdularis on 14/11/17.
 */

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }

        Request req = chain.request();
        return chain.proceed(req);
    }
}
