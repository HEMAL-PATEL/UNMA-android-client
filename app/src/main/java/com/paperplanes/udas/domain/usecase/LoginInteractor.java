package com.paperplanes.udas.domain.usecase;

import com.paperplanes.udas.domain.SessionManager;
import com.paperplanes.udas.domain.data.AuthService;
import com.paperplanes.udas.domain.executor.ExecutionScheduler;
import com.paperplanes.udas.domain.executor.PostExecutionScheduler;
import com.paperplanes.udas.domain.model.LoginModel;
import com.paperplanes.udas.domain.model.Session;

import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by abdularis on 06/11/17.
 */

public class LoginInteractor extends BaseInteractor<LoginModel, LoginInteractor.Params> {

    private AuthService mAuthService;
    private SessionManager mSessionManager;

    @Inject
    public LoginInteractor(AuthService authService,
                           SessionManager sessionManager,
                           ExecutionScheduler execution,
                           PostExecutionScheduler postExecution) {
        super(execution, postExecution);
        mSessionManager = sessionManager;
        mAuthService = authService;
    }

    @Override
    protected Observable<LoginModel> buildObservable(LoginInteractor.Params params) {
        return mAuthService
                .login(params.username, params.password)
                .doOnSuccess(loginModel -> {
                    if (loginModel.isSuccess()) {
                        Session sess = new Session();
                        sess.setAccessToken(loginModel.getAccessToken());
                        sess.setExpire(new Date(new Timestamp(loginModel.getExpire()).getTime()));
                        mSessionManager.setSession(sess);
                    }
                })
                .toObservable();
    }

    public static class Params {
        public String username;
        public String password;
    }

}
