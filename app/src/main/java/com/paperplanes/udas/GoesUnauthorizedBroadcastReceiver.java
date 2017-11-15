package com.paperplanes.udas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.paperplanes.udas.login.LoginActivity;

/**
 * Created by abdularis on 15/11/17.
 */

public class GoesUnauthorizedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("UnmaApp", "receive unauthorized access: starting login activity...");
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
