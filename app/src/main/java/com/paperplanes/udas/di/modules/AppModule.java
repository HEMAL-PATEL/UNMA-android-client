package com.paperplanes.udas.di.modules;

import android.app.Application;
import android.content.Context;

import com.paperplanes.udas.data.AnnouncementRepository;
import com.paperplanes.udas.data.NetAuthService;
import com.paperplanes.udas.data.api.AnnouncementApi;
import com.paperplanes.udas.data.api.AuthApi;
import com.paperplanes.udas.data.api.ServiceGenerator;
import com.paperplanes.udas.data.api.UpdateTokenApi;
import com.paperplanes.udas.domain.SessionManager;
import com.paperplanes.udas.domain.data.AnnouncementDataSource;
import com.paperplanes.udas.domain.data.AuthService;
import com.paperplanes.udas.domain.executor.ExecutionScheduler;
import com.paperplanes.udas.domain.executor.PostExecutionScheduler;
import com.paperplanes.udas.presentation.AndroidIoThread;
import com.paperplanes.udas.presentation.AndroidMainThread;
import com.paperplanes.udas.presentation.AndroidSessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    @Singleton
    ExecutionScheduler provideExecutionScheduler() {
        return new AndroidIoThread();
    }

    @Provides
    @Singleton
    PostExecutionScheduler providePostExecutionScheduler() {
        return new AndroidMainThread();
    }

    @Provides
    AuthService provideAuthService(AuthApi authApi) {
        return new NetAuthService(authApi);
    }

    @Provides
    @Singleton
    AuthApi provideAuthApi() {
        return ServiceGenerator.createService(AuthApi.class);
    }

    @Provides
    @Singleton
    UpdateTokenApi provideUpdateTokenApi() {
        return ServiceGenerator.createService(UpdateTokenApi.class);
    }

    @Provides
    @Singleton
    AnnouncementApi provideAnnouncementApi() {
        return ServiceGenerator.createService(AnnouncementApi.class);
    }

    @Provides
    @Singleton
    AnnouncementDataSource provideAnnouncementDataSource(AnnouncementApi announcementApi, SessionManager sessionManager) {
        return new AnnouncementRepository(announcementApi, sessionManager);
    }
}
