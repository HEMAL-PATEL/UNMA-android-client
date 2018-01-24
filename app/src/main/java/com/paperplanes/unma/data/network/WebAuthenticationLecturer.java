package com.paperplanes.unma.data.network;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.network.api.AuthenticationApi;

/**
 * Created by abdularis on 24/01/18.
 */

public class WebAuthenticationLecturer extends WebAuthentication {

    public WebAuthenticationLecturer(AuthenticationApi authApi, SessionManager sessionManager) {
        super(authApi, sessionManager);
    }

    @Override
    protected int getUserType() {
        return 2;
    }
}
