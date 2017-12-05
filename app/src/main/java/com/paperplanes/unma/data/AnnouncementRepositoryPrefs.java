package com.paperplanes.unma.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abdularis on 24/11/17.
 */

public class AnnouncementRepositoryPrefs {
    private SharedPreferences mPrefs;

    private static final String KEY_SINCE_UPDATED = "SINCE_UPDATED";

    public AnnouncementRepositoryPrefs(Context context) {
        mPrefs = context.getSharedPreferences("AnnouncementRepositoryPrefs", Context.MODE_PRIVATE);
    }

    public long getSinceUpdated() {
        return mPrefs.getLong(KEY_SINCE_UPDATED, 0);
    }

    public void setSinceUpdated(long sinceUpdated) {
        mPrefs.edit()
                .putLong(KEY_SINCE_UPDATED, sinceUpdated)
                .apply();
    }

}
