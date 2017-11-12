package com.paperplanes.udas.domain.executor;

import io.reactivex.Scheduler;

/**
 * Created by abdularis on 06/11/17.
 */

public interface ExecutionScheduler {
    Scheduler getScheduler();
}
