package com.paperplanes.udas.presentation;

import com.paperplanes.udas.domain.executor.ExecutionScheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by abdularis on 06/11/17.
 */

public class AndroidIoThread implements ExecutionScheduler {
    @Override
    public Scheduler getScheduler() {
        return Schedulers.io();
    }
}
