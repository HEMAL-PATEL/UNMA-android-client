package com.paperplanes.unma;

import android.content.Context;

/**
 * Created by abdularis on 18/11/17.
 */

public class ResourceProvider {

    private Context mAppContext;

    public ResourceProvider(Context context) {
        mAppContext = context;
    }

    public String getString(int resId) {
        return mAppContext.getString(resId);
    }

}
