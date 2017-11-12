package com.paperplanes.udas.domain.usecase;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by abdularis on 06/11/17.
 */

public class DefaultObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable disposable) {

    }

    @Override
    public void onNext(T obj) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
