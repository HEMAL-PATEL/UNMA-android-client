package com.paperplanes.unma.data;

import com.paperplanes.unma.common.Optional;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

import java.util.List;

public interface ReactiveStore<Key, Value> {

    void storeSingular(@NonNull final Value model) throws Exception;

    void storeAll(@NonNull final List<Value> modelList) throws Exception;

    void replaceAll(@NonNull final List<Value> modelList) throws Exception;

    Flowable<Optional<Value>> getSingular(@NonNull final Key key);

    Flowable<Optional<List<Value>>> getAll();

    void clearAll();

}
