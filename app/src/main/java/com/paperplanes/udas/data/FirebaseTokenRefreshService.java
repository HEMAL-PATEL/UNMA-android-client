package com.paperplanes.udas.data;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.paperplanes.udas.App;
import com.paperplanes.udas.data.api.ServiceGenerator;
import com.paperplanes.udas.data.api.UpdateTokenApi;
import com.paperplanes.udas.data.api.response.JsonResp;
import com.paperplanes.udas.di.components.DaggerServiceComponent;
import com.paperplanes.udas.domain.SessionManager;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

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

        ((App) getApplication()).getServiceComponent().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if (!mSessionManager.isSessionSet()) {
            return;
        }

        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "New Firebase Token: " + fcmToken);

        mUpdateTokenApi
                .updateTokenOnServer(mSessionManager.getSession().getAccessToken(), fcmToken)
                .subscribe(resp -> Log.d(TAG, "Firebase token updated: " + resp.getMessage()),
                        throwable -> Log.d(TAG, "Failed to update firebase token: " + throwable.getMessage()));
    }
}
