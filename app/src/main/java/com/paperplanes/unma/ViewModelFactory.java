package com.paperplanes.unma;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.paperplanes.unma.announcementdetail.AnnouncementDetailViewModel;
import com.paperplanes.unma.announcementlist.AnnouncementListViewModel;
import com.paperplanes.unma.announcementmedialist.MediaListViewModel;
import com.paperplanes.unma.login.LoginViewModel;
import com.paperplanes.unma.main.MainViewModel;

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

    @Inject
    public ViewModelFactory(LoginViewModel loginViewModel,
                            MainViewModel mainViewModel,
                            AnnouncementListViewModel listViewModel,
                            AnnouncementDetailViewModel detailViewModel,
                            MediaListViewModel mediaListViewModel) {
        mLoginViewModel = loginViewModel;
        mMainViewModel = mainViewModel;
        mAnnouncementListViewModel = listViewModel;
        mAnnouncementDetailViewModel = detailViewModel;
        mMediaListViewModel = mediaListViewModel;
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
        throw new IllegalArgumentException("Unknown class name");
    }
}
