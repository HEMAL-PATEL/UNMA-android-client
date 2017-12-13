package com.paperplanes.unma;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.paperplanes.unma.announcementdetail.AnnouncementDetailViewModel;
import com.paperplanes.unma.announcementlist.AnnouncementListViewModel;
import com.paperplanes.unma.announcementmedialist.MediaListViewModel;
import com.paperplanes.unma.login.LoginViewModel;
import com.paperplanes.unma.main.MainViewModel;
import com.paperplanes.unma.profiledetail.ProfileDetailViewModel;
import com.paperplanes.unma.profileupdate.ProfileUpdateViewModel;

import javax.inject.Inject;

/**
 * Created by abdularis on 16/11/17.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {

    private LoginViewModel mLoginViewModel;
    private MainViewModel mMainViewModel;
    private AnnouncementListViewModel mAnnouncementListViewModel;
    private AnnouncementDetailViewModel mAnnouncementDetailViewModel;
    private MediaListViewModel mMediaListViewModel;
    private ProfileDetailViewModel mProfileDetailViewModel;
    private ProfileUpdateViewModel mProfileUpdateViewModel;

    @Inject
    public ViewModelFactory(LoginViewModel loginViewModel,
                            MainViewModel mainViewModel,
                            AnnouncementListViewModel listViewModel,
                            AnnouncementDetailViewModel detailViewModel,
                            MediaListViewModel mediaListViewModel,
                            ProfileDetailViewModel profileDetailViewModel,
                            ProfileUpdateViewModel profileUpdateViewModel) {
        mLoginViewModel = loginViewModel;
        mMainViewModel = mainViewModel;
        mAnnouncementListViewModel = listViewModel;
        mAnnouncementDetailViewModel = detailViewModel;
        mMediaListViewModel = mediaListViewModel;
        mProfileDetailViewModel = profileDetailViewModel;
        mProfileUpdateViewModel = profileUpdateViewModel;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) mLoginViewModel;
        else if (modelClass.isAssignableFrom(MainViewModel.class))
            return (T) mMainViewModel;
        else if (modelClass.isAssignableFrom(AnnouncementListViewModel.class))
            return (T) mAnnouncementListViewModel;
        else if (modelClass.isAssignableFrom(AnnouncementDetailViewModel.class))
            return (T) mAnnouncementDetailViewModel;
        else if (modelClass.isAssignableFrom(MediaListViewModel.class))
            return (T) mMediaListViewModel;
        else if (modelClass.isAssignableFrom(ProfileDetailViewModel.class))
            return (T) mProfileDetailViewModel;
        else if (modelClass.isAssignableFrom(ProfileUpdateViewModel.class))
            return (T) mProfileUpdateViewModel;
        throw new IllegalArgumentException("Unknown class name");
    }
}
