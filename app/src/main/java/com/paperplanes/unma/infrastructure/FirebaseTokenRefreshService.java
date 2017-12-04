package com.paperplanes.unma.infrastructure;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.paperplanes.unma.App;
import com.paperplanes.unma.data.network.api.UpdateTokenApi;
import com.paperplanes.unma.auth.SessionManager;

import javax.inject.Inject;

/**
 * Created by abdularis on 07/11/17.
 */

public class FirebaseTokenRefreshService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseTokenRefreshService.class.getSimpleName();

    @Inject
    UpdateTokenApi mUpdateTokenApi;

    @Inject
    SessionManager mSessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if (!mSessionManager.isSessionSet()) {
            return;
        }

        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "New firebase token: " + fcmToken);

        mUpdateTokenApi
                .updateTokenOnServer(mSessionManager.getSession().getAccessToken(), fcmToken)
                .subscribe(resp -> Log.d(TAG, "Firebase token updated: " + resp.getMessage()),
                        throwable -> Log.d(TAG, "Failed to update firebase token: " + throwable.getMessage()));
    }
}
