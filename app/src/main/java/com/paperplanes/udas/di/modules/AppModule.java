package com.paperplanes.udas.di.modules;

import android.app.Application;
import android.content.Context;

import com.paperplanes.udas.common.AuthInterceptor;
import com.paperplanes.udas.common.ConnectivityInterceptor;
import com.paperplanes.udas.data.AnnouncementRepository;
import com.paperplanes.udas.data.MemoryAnnouncementStore;
import com.paperplanes.udas.data.network.WebAuthentication;
import com.paperplanes.udas.data.network.api.AnnouncementApi;
import com.paperplanes.udas.data.network.api.AuthApi;
import com.paperplanes.udas.data.network.api.WebServiceGenerator;
import com.paperplanes.udas.data.network.api.UpdateTokenApi;
import com.paperplanes.udas.auth.SessionManager;
import com.paperplanes.udas.auth.Authentication;
import com.paperplanes.udas.AndroidSessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by abdularis on 02/11/17.
 */

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    SessionManager provideSession(Context context) {
        return new AndroidSessionManager(context);
    }

    @Provides
    Authentication provideAuthService(AuthApi authApi, SessionManager sessionManager) {
        return new WebAuthentication(authApi, sessionManager);
    }

    @Provides
    @Singleton
    WebServiceGenerator provideWebServiceGenerator(Context context, SessionManager sessionManager) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(context))
                .addInterceptor(new AuthInterceptor(context, sessionManager))
                .build();
        return new WebServiceGenerator(httpClient);
    }

    @Provides
    @Singleton
    AuthApi provideAuthApi(WebServiceGenerator webServiceGenerator) {
        return webServiceGenerator.createService(AuthApi.class);
    }

    @Provides
    @Singleton
    UpdateTokenApi provideUpdateTokenApi(WebServiceGenerator webServiceGenerator) {
        return webServiceGenerator.createService(UpdateTokenApi.class);
    }

    @Provides
    @Singleton
    AnnouncementApi provideAnnouncementApi(WebServiceGenerator webServiceGenerator) {
        return webServiceGenerator.createService(AnnouncementApi.class);
    }

    @Provides
    @Singleton
    AnnouncementRepository provideAnnouncementRepository(AnnouncementApi api) {
        return new AnnouncementRepository(new MemoryAnnouncementStore(), api);
    }
}
