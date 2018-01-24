package com.paperplanes.unma.login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.R;
import com.paperplanes.unma.ResourceProvider;
import com.paperplanes.unma.auth.Authentication;
import com.paperplanes.unma.common.exceptions.NoConnectivityException;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.model.LoginResult;
import com.paperplanes.unma.model.Profile;

import java.net.SocketException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by abdularis on 15/11/17.
 */

public class LoginViewModel extends ViewModel {

    private Authentication mAuthStudent;
    private Authentication mAuthLecturer;
    private CompositeDisposable mCompositeDisposable;
    private ResourceProvider mResourceProvider;

    private SingleLiveEvent<Boolean> mLoading;
    private SingleLiveEvent<String> mUsernameErr;
    private SingleLiveEvent<String> mPasswordErr;
    private SingleLiveEvent<String> mGeneralErr;
    private SingleLiveEvent<LoginResult> mLoginResult;

    @Inject
    public LoginViewModel(ResourceProvider resourceProvider,
                          Authentication authenticationForStudent,
                          Authentication authenticationForLecturer) {
        mAuthStudent = authenticationForStudent;
        mAuthLecturer = authenticationForLecturer;
        mResourceProvider = resourceProvider;

        mLoading = new SingleLiveEvent<>();
        mUsernameErr = new SingleLiveEvent<>();
        mPasswordErr = new SingleLiveEvent<>();
        mGeneralErr = new SingleLiveEvent<>();
        mLoginResult = new SingleLiveEvent<>();
    }

    @Override
    protected void onCleared() {
        if (mCompositeDisposable != null)
            mCompositeDisposable.dispose();
    }

    void login(@NonNull String username, @NonNull String password, int userType) {
        String regex = "(\\d{2}\\.){2}\\d\\.\\d{4}";
        if (userType == 2)
            regex = "\\d{10}";

        if (username.isEmpty()) {
            if (userType == Profile.USER_TYPE_STUDENT)
                mUsernameErr.setValue(mResourceProvider.getString(R.string.err_username_empty));
            else
                mUsernameErr.setValue(mResourceProvider.getString(R.string.err_username_empty_2));
            return;
        }
        else if (!username.matches(regex)) {
            if (userType == Profile.USER_TYPE_STUDENT)
                mUsernameErr.setValue(mResourceProvider.getString(R.string.err_malformed_username));
            else
                mUsernameErr.setValue(mResourceProvider.getString(R.string.err_malformed_username_2));
            return;
        }

        if (password.isEmpty()) {
            mPasswordErr.setValue(mResourceProvider.getString(R.string.err_password_empty));
            return;
        }

        Authentication auth = mAuthStudent;
        if (userType == Profile.USER_TYPE_LECTURER)
            auth = mAuthLecturer;

        prepareCompositeDisposable();
        mLoading.setValue(true);
        mCompositeDisposable.add(auth.login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mLoginResult.setValue(loginResult);
                        mLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLoading.setValue(false);
                        if (throwable instanceof NoConnectivityException) {
                            mGeneralErr.setValue(mResourceProvider.getString(R.string.err_no_connectivity));
                        } else if (throwable instanceof SocketException) {
                            mGeneralErr.setValue(mResourceProvider.getString(R.string.err_cant_connect));
                        } else {
                            mGeneralErr.setValue(mResourceProvider.getString(R.string.err_unknown));
                        }
                    }
                }));
    }

    MutableLiveData<Boolean> getLoading() {
        return mLoading;
    }

    MutableLiveData<String> getUsernameErr() {
        return mUsernameErr;
    }

    MutableLiveData<String> getPasswordErr() {
        return mPasswordErr;
    }

    MutableLiveData<String> getGeneralErr() {
        return mGeneralErr;
    }

    MutableLiveData<LoginResult> getLoginResult() {
        return mLoginResult;
    }

    private void prepareCompositeDisposable() {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
    }
}
