package com.paperplanes.udas.presentation;

import com.paperplanes.udas.domain.executor.PostExecutionScheduler;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by abdularis on 06/11/17.
 */

public class AndroidMainThread implements PostExecutionScheduler {
    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
