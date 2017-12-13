package com.paperplanes.unma.di.components;

import com.paperplanes.unma.App;
import com.paperplanes.unma.announcementdetail.AnnouncementDetailActivity;
import com.paperplanes.unma.announcementlist.AnnouncementListFragment;
import com.paperplanes.unma.data.DownloadManagerService;
import com.paperplanes.unma.infrastructure.FirebaseNotificationService;
import com.paperplanes.unma.infrastructure.FirebaseTokenRefreshService;
import com.paperplanes.unma.di.modules.AppModule;
import com.paperplanes.unma.login.LoginActivity;
import com.paperplanes.unma.main.MainActivity;
import com.paperplanes.unma.announcementmedialist.MediaListFragment;
import com.paperplanes.unma.profiledetail.ProfileDetailFragment;
import com.paperplanes.unma.profileupdate.ProfileUpdateActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 02/11/17.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(App client);

    void inject(LoginActivity client);

    void inject(AnnouncementListFragment client);

    void inject(MediaListFragment client);

    void inject(ProfileDetailFragment client);

    void inject(AnnouncementDetailActivity client);

    void inject(ProfileUpdateActivity client);

    void inject(MainActivity client);

    void inject(FirebaseTokenRefreshService client);

    void inject(FirebaseNotificationService client);

    void inject(DownloadManagerService client);

}
