package com.paperplanes.udas.domain.usecase;

import com.paperplanes.udas.domain.SessionManager;
import com.paperplanes.udas.domain.data.AuthService;
import com.paperplanes.udas.domain.executor.ExecutionScheduler;
import com.paperplanes.udas.domain.executor.PostExecutionScheduler;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by abdularis on 06/11/17.
 */

public class LogoutInteractor extends BaseInteractor<Boolean, Void> {

    private AuthService mAuthService;
    private SessionManager mSessionManager;

    @Inject
    public LogoutInteractor(AuthService authService,
                            SessionManager sessionManager,
                            ExecutionScheduler execution,
                            PostExecutionScheduler postExecution) {
        super(execution, postExecution);
        mSessionManager = sessionManager;
        mAuthService = authService;
    }

    @Override
    protected Observable<Boolean> buildObservable(Void params) {
        if (!mSessionManager.isSessionSet()) {
            return Observable.just(false);
        }

        return mAuthService.logout(mSessionManager.getSession().getAccessToken())
                .doOnSuccess(success -> {
                    if (success) {
                        mSessionManager.setSession(null);
                    }
                })
                .toObservable();
    }
}
