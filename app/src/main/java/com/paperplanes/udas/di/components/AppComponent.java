package com.paperplanes.udas.di.components;

import com.paperplanes.udas.announcementlist.AnnouncementListFragment;
import com.paperplanes.udas.auth.UnmaAuthenticatorActivity;
import com.paperplanes.udas.di.modules.AppModule;
import com.paperplanes.udas.login.LoginActivity;
import com.paperplanes.udas.main.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 02/11/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(LoginActivity client);

    void inject(UnmaAuthenticatorActivity client);

    void inject(AnnouncementListFragment client);

    void inject(MainActivity client);
}
