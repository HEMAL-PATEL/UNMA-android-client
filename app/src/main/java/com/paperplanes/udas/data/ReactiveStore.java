package com.paperplanes.udas.data;

import com.paperplanes.udas.model.Announcement;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

import java.util.List;

public interface ReactiveStore<Key, Value> {

    void storeSingular(@NonNull final Value model);

    void storeAll(@NonNull final List<Value> modelList);

    void replaceAll(@NonNull final List<Value> modelList);

    Flowable<Value> getSingular(@NonNull final Key key);

    Flowable<List<Value>> getAll();

    interface AnnouncementStore extends ReactiveStore<String, Announcement> {

    }

}
