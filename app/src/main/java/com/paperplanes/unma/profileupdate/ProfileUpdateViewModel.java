package com.paperplanes.unma.profileupdate;

import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.R;
import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Session;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.common.exceptions.HandledRuntimeError;
import com.paperplanes.unma.data.ProfileRepository;
import com.paperplanes.unma.model.ProfileUpdateResult;

import java.sql.Timestamp;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by abdularis on 08/12/17.
 */

public class ProfileUpdateViewModel extends ViewModel {

    private SingleLiveEvent<Boolean> mLoading;
    private SingleLiveEvent<Throwable> mError;
    private SingleLiveEvent<Void> mSuccess;

    private ResourceProvider mResourceProvider;
    private ProfileRepository mRepo;
    private SessionManager mSessionManager;

    public ProfileUpdateViewModel(ResourceProvider resourceProvider,
                                  ProfileRepository repository,
                                  SessionManager sessionManager) {
        mRepo = repository;
        mResourceProvider = resourceProvider;
        mLoading = new SingleLiveEvent<>();
        mError = new SingleLiveEvent<>();
        mSuccess = new SingleLiveEvent<>();
        mSessionManager = sessionManager;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            mError.setValue(new HandledRuntimeError(mResourceProvider.getString(R.string.update_password_empty_error)));
            return;
        }

        if (oldPassword.equals(newPassword)) {
            mError.setValue(new HandledRuntimeError(mResourceProvider.getString(R.string.update_password_same_pass)));
            return;
        }

        mLoading.setValue(true);
        mRepo.updatePassword(oldPassword, newPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ProfileUpdateResult>() {
                    @Override
                    public void onSuccess(ProfileUpdateResult result) {
                        mLoading.setValue(false);
                        if (result.isSuccess()) {
                            Session sess = new Session();
                            sess.setAccessToken(result.getAccessToken());
                            sess.setExpire(new Date(new Timestamp(result.getExpire()).getTime()));
                            sess.setName(result.getName());
                            sess.setUsername(result.getUsername());
                            mSessionManager.setSession(sess);
                            mSuccess.call();
                        }
                        else {
                            mError.setValue(new HandledRuntimeError(result.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLoading.setValue(false);
                        mError.setValue(throwable);
                    }
                });
    }

    public SingleLiveEvent<Boolean> getLoading() {
        return mLoading;
    }

    public SingleLiveEvent<Throwable> getError() {
        return mError;
    }

    public SingleLiveEvent<Void> getSuccess() {
        return mSuccess;
    }
}
