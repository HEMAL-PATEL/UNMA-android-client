package com.paperplanes.udas.domain;

import com.paperplanes.udas.domain.model.Session;

public abstract class SessionManager {

    public abstract void setSession(Session session);

    public abstract boolean isSessionSet();

    public abstract Session getSession();

}
