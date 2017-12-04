package com.paperplanes.unma;


import android.content.Context;
import android.content.SharedPreferences;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.auth.Session;

import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by abdularis on 06/11/17.
 */

public class AndroidSessionManager extends SessionManager {

    private static final String PREF_NAME = "UdasSession";
    private static final String KEY_ACC_TOKEN = "AccToken";
    private static final String KEY_EXPIRE = "Exp";
    private static final String KEY_NAME = "Name";
    private static final String KEY_USERNAME = "Username";

    private SharedPreferences mPrefs;
    private Session mSession;

    @Inject
    public AndroidSessionManager(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        readSavedSession();
    }

    private void readSavedSession() {
        String accToken = mPrefs.getString(KEY_ACC_TOKEN, null);
        long exp = mPrefs.getLong(KEY_EXPIRE, 0);

        if (accToken == null || exp == 0) {
            return;
        }

        mSession = new Session();
        mSession.setAccessToken(accToken);
        mSession.setExpire(new Date(new Timestamp(exp).getTime()));
        mSession.setName(mPrefs.getString(KEY_NAME, ""));
        mSession.setUsername(mPrefs.getString(KEY_USERNAME, ""));
    }

    @Override
    public void setSession(Session session) {
        super.setSession(session);
        if (session != null) {
            mSession = session;
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(KEY_ACC_TOKEN, session.getAccessToken());
            editor.putLong(KEY_EXPIRE, session.getExpire().getTime());
            editor.putString(KEY_NAME, session.getName());
            editor.putString(KEY_USERNAME, session.getUsername());
            editor.apply();
        }
    }

    @Override
    public boolean isSessionSet() {
        return mSession != null;
    }

    @Override
    public Session getSession() {
        return mSession;
    }

    @Override
    public void clearSession(LogoutEvent.Cause cause) {
        super.clearSession(cause);

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(KEY_ACC_TOKEN, null);
        editor.putLong(KEY_EXPIRE, 0);
        editor.putString(KEY_NAME, null);
        editor.putString(KEY_USERNAME, null);
        editor.apply();
        mSession = null;
    }
}
