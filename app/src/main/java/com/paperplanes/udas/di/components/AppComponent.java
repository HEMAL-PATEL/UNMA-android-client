package com.paperplanes.udas.di.components;

import com.paperplanes.udas.di.modules.AppModule;
import com.paperplanes.udas.presentation.activity.AnnouncementsActivity;
import com.paperplanes.udas.presentation.activity.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 02/11/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(LoginActivity client);

    void inject(AnnouncementsActivity client);

}
