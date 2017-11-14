package com.paperplanes.udas.di.components;

import com.paperplanes.udas.data.FirebaseTokenRefreshService;
import com.paperplanes.udas.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 07/11/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface ServiceComponent {

    void inject(FirebaseTokenRefreshService client);

}
