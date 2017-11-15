package com.paperplanes.udas.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by abdularis on 15/11/17.
 */

public class UnmaAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        UnmaAuthenticator auth = new UnmaAuthenticator(this);
        return auth.getIBinder();
    }
}
