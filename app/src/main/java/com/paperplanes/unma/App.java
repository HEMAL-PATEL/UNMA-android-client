package com.paperplanes.unma;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.di.components.AppComponent;
import com.paperplanes.unma.di.components.DaggerAppComponent;
import com.paperplanes.unma.di.modules.AppModule;
import com.paperplanes.unma.login.LoginActivity;

import javax.inject.Inject;

import abdularis.github.com.materialcolorrandomizer.MaterialColorRandom;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by abdularis on 02/11/17.
 */

public class App extends Application {

    private AppComponent mAppComponent;

    @Inject
    SessionManager mSessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        AppModule appModule = new AppModule(this);
        mAppComponent = DaggerAppComponent.builder().appModule(appModule).build();

        mAppComponent.inject(this);

        mSessionManager.observeOnLogout(
                AndroidSchedulers.mainThread(),
                SessionManager.LogoutEvent.Cause.UnIntentional,
                event -> {
                    Intent i = new Intent(App.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    Toast.makeText(App.this, "Network Unauthorized", Toast.LENGTH_SHORT).show();
                }
        );

        // clear saved material color
        mSessionManager.observeOnLogout(
                AndroidSchedulers.mainThread(),
                logoutEvent -> MaterialColorRandom.getInstance(App.this).clearColors()
        );
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
