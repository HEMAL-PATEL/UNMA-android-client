package com.paperplanes.unma;

import android.app.Application;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.di.components.AppComponent;
import com.paperplanes.unma.di.components.DaggerAppComponent;
import com.paperplanes.unma.di.modules.AppModule;
import com.paperplanes.unma.login.LoginActivity;

import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;

/**
 * Created by abdularis on 02/11/17.
 */

public class App extends Application {

    private AppComponent mAppComponent;

    @Inject
    SessionManager mSessionManager;

    @Inject
    OkHttpClient mOkHttpClient;

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
                }
        );

        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
