package com.paperplanes.unma.data.network;

import com.google.firebase.iid.FirebaseInstanceId;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.network.WebAuthentication;
import com.paperplanes.unma.data.network.api.AuthenticationApi;
import com.paperplanes.unma.model.LoginResult;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 24/01/18.
 */

public class WebAuthenticationStudent extends WebAuthentication {

    public WebAuthenticationStudent(AuthenticationApi authApi, SessionManager sessionManager) {
        super(authApi, sessionManager);
    }

    @Override
    protected int getUserType() {
        return 1;
    }

}
