package com.paperplanes.unma.common;

import android.content.Context;

import com.paperplanes.unma.R;
import com.paperplanes.unma.common.exceptions.NoConnectivityException;

import java.net.SocketException;

/**
 * Created by abdularis on 30/11/17.
 */

public final class ErrorUtil {

    public static String getErrorStringForThrowable(Context context, Throwable throwable) {
        if (throwable instanceof NoConnectivityException) {
            return context.getResources().getString(R.string.err_no_connectivity);
        } else if (throwable instanceof SocketException) {
            return context.getResources().getString(R.string.err_cant_connect);
        }
        return context.getResources().getString(R.string.err_unknown);
    }

}
