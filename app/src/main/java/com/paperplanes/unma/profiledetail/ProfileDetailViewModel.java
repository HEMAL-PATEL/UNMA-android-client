package com.paperplanes.unma.profiledetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.data.ProfileRepository;
import com.paperplanes.unma.model.Profile;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by abdularis on 06/12/17.
 */

public class ProfileDetailViewModel extends ViewModel {

    private MutableLiveData<Profile> mProfile;
    private SingleLiveEvent<Throwable> mError;
    private SingleLiveEvent<Boolean> mLoading;
    private ProfileRepository mRepo;

    private boolean mShouldRefresh;

    public ProfileDetailViewModel(ProfileRepository repo, SessionManager sessionManager) {
        mRepo = repo;
        mProfile = new MutableLiveData<>();
        mError = new SingleLiveEvent<>();
        mLoading = new SingleLiveEvent<>();
        sessionManager
                .observeOnLogout(
                        AndroidSchedulers.mainThread(),
                        event -> {
                            mProfile.setValue(null);
                            mRepo.clear();
                        });
        mShouldRefresh = true;
    }

    public void loadProfile() {
        mLoading.setValue(true);
        mRepo.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<Optional<Profile>>() {
                    @Override
                    public void onNext(Optional<Profile> profileOptional) {
                        if (profileOptional.isNotNull()) {
                            mProfile.setValue(profileOptional.get());
                        }

                        if (mShouldRefresh) {
                            mShouldRefresh = false;
                            refresh();
                        }
                        else {
                            mLoading.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        mLoading.setValue(false);
                        mError.setValue(t);
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    private void refresh() {
        mRepo.fetch()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLoading.setValue(false);
                        mError.setValue(throwable);
                    }
                });
    }

    public SingleLiveEvent<Throwable> getError() {
        return mError;
    }

    public SingleLiveEvent<Boolean> getLoading() {
        return mLoading;
    }

    public MutableLiveData<Profile> getProfile() {
        return mProfile;
    }
}
