package com.paperplanes.unma;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.paperplanes.unma.auth.Session;
import com.paperplanes.unma.auth.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by abdularis on 20/01/18.
 */

@RunWith(AndroidJUnit4.class)
public class AndroidSessionManagerTest {

    AndroidSessionManager sessMan;

    @Before
    public void setUp() {
        InstrumentationRegistry.getTargetContext().getSharedPreferences("UdasSession", Context.MODE_PRIVATE).edit().clear().apply();
    }

    private void loadSessManager() {
        sessMan = new AndroidSessionManager(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void setSession_test() {
        loadSessManager();

        assertNull(sessMan.getSession());

        Session session = new Session();
        session.setName("sess_name");
        session.setUsername("user_name");
        session.setAccessToken("acc_token");
        session.setExpire(new Date());

        sessMan.setSession(session);
        loadSessManager();
        assertNotNull(sessMan.getSession());
        assertTrue(sessMan.isSessionSet());

        sessMan.clearSession(SessionManager.LogoutEvent.Cause.Intentional);
        assertNull(sessMan.getSession());
    }
}
