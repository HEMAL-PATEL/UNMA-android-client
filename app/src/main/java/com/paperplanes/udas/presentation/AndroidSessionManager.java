package com.paperplanes.udas.presentation;


import android.content.Context;
import android.content.SharedPreferences;

import com.paperplanes.udas.domain.SessionManager;
import com.paperplanes.udas.domain.model.Session;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by abdularis on 06/11/17.
 */

public class AndroidSessionManager extends SessionManager {

    private static final String PREF_NAME = "UdasSession";
    private static final String KEY_ACC_TOKEN = "AccToken";
    private static final String KEY_EXPIRE = "Exp";

    private SharedPreferences mPrefs;
    private Session mSession;

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
    }

    @Override
    public void setSession(Session session) {
        mSession = session;
        SharedPreferences.Editor editor = mPrefs.edit();
        if (session == null) {
            editor.putString(KEY_ACC_TOKEN, null);
            editor.putLong(KEY_EXPIRE, 0);
        }
        else {
            editor.putString(KEY_ACC_TOKEN, session.getAccessToken());
            editor.putLong(KEY_EXPIRE, session.getExpire().getTime());
        }
        editor.apply();
    }

    @Override
    public boolean isSessionSet() {
        return mSession != null;
    }

    @Override
    public Session getSession() {
        return mSession;
    }
}
