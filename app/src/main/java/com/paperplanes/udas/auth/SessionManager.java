package com.paperplanes.udas.auth;

public interface SessionManager {

    void setSession(Session session);

    boolean isSessionSet();

    Session getSession();

    void clearSession();

}
