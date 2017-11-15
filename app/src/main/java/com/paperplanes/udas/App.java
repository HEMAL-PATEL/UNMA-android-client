package com.paperplanes.udas;

import android.app.Application;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.paperplanes.udas.di.components.AppComponent;
import com.paperplanes.udas.di.components.DaggerAppComponent;
import com.paperplanes.udas.di.components.DaggerServiceComponent;
import com.paperplanes.udas.di.components.ServiceComponent;
import com.paperplanes.udas.di.modules.AppModule;

/**
 * Created by abdularis on 02/11/17.
 */

public class App extends Application {

    private AppComponent mAppComponent;
    private ServiceComponent mServiceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder().appModule(appModule).build();
        mServiceComponent = DaggerServiceComponent.builder().appModule(appModule).build();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(new GoesUnauthorizedBroadcastReceiver(),
                        new IntentFilter("UnauthorizedAccess"));
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public ServiceComponent getServiceComponent() {
        return mServiceComponent;
    }
}
