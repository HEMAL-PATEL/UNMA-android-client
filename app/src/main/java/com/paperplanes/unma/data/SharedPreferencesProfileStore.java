package com.paperplanes.unma.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.paperplanes.unma.model.Profile;

/**
 * Created by abdularis on 07/12/17.
 */

public class SharedPreferencesProfileStore implements ProfileStore {

    private static final String KEY_PROF_NAME = "NAME";
    private static final String KEY_PROF_USERNAME = "USERNAME";
    private static final String KEY_PROF_CLS_PROGRAM = "CLS_PROGRAM";
    private static final String KEY_PROF_CLS_NAME = "CLS_NAME";
    private static final String KEY_PROF_CLS_YEAR = "CLS_YEAR";
    private static final String KEY_PROF_CLS_TYPE = "CLS_TYPE";

    private static final String PREF_NAME = "profile";

    private SharedPreferences mPref;

    public SharedPreferencesProfileStore(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void store(Profile profile) {
        Profile.ProfileClass cls = profile.getProfileClass();
        mPref.edit()
                .putString(KEY_PROF_NAME, profile.getName())
                .putString(KEY_PROF_USERNAME, profile.getUsername())
                .putString(KEY_PROF_CLS_PROGRAM, cls.getStudyProgram())
                .putString(KEY_PROF_CLS_NAME, cls.getClassName())
                .putInt(KEY_PROF_CLS_YEAR, cls.getClassYear())
                .putString(KEY_PROF_CLS_TYPE, cls.getClassType())
                .apply();
    }

    @Override
    public Profile get() {
        String name = mPref.getString(KEY_PROF_NAME, null);
        String username = mPref.getString(KEY_PROF_USERNAME, null);
        if (name == null || username == null) return null;
        Profile.ProfileClass cls = new Profile.ProfileClass(
                mPref.getString(KEY_PROF_CLS_PROGRAM, ""),
                mPref.getString(KEY_PROF_CLS_NAME, ""),
                mPref.getInt(KEY_PROF_CLS_YEAR, 0),
                mPref.getString(KEY_PROF_CLS_TYPE, "")
        );

        return new Profile(
                name,
                username,
                cls
        );
    }

    @Override
    public void clear() {
        mPref.edit()
                .putString(KEY_PROF_NAME, null)
                .putString(KEY_PROF_USERNAME, null)
                .putString(KEY_PROF_CLS_PROGRAM, null)
                .putString(KEY_PROF_CLS_NAME, null)
                .putInt(KEY_PROF_CLS_YEAR, 0)
                .putString(KEY_PROF_CLS_TYPE, null)
                .apply();
    }
}
