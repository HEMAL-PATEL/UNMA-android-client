package com.paperplanes.udas.domain.usecase;


import com.paperplanes.udas.domain.executor.ExecutionScheduler;
import com.paperplanes.udas.domain.executor.PostExecutionScheduler;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by abdularis on 06/11/17.
 */

public abstract class BaseInteractor <T, ParamsType> {

    private ExecutionScheduler mExecutionThread;
    private PostExecutionScheduler mPostExecutionThread;

    public BaseInteractor(ExecutionScheduler execution, PostExecutionScheduler postExecution) {
        mExecutionThread = execution;
        mPostExecutionThread = postExecution;
    }

    public Observable<T> exec(ParamsType params) {
        return buildObservable(params)
                .subscribeOn(mExecutionThread.getScheduler())
                .observeOn(mPostExecutionThread.getScheduler());
    }

    protected abstract Observable<T> buildObservable(ParamsType params);

}
