package com.paperplanes.udas.data;

import android.util.Log;

import com.paperplanes.udas.model.Announcement;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryAnnouncementStore implements ReactiveStore.AnnouncementStore {

    public FlowableProcessor<List<Announcement>> mProcessor;

    public HashMap<String, Announcement> mCache;

    public MemoryAnnouncementStore() {
        mProcessor = PublishProcessor.create();
        mCache = new HashMap<>();
    }

    @Override
    public void storeSingular(Announcement model) {

    }

    @Override
    public void storeAll(List<Announcement> modelList) {
        for (Announcement ann : modelList) {
            mCache.put(ann.getId(), ann);
        }

        Log.v("ThreadName", "storeAll: " + Thread.currentThread().getName());
        mProcessor.onNext(modelList);
        Log.v("ThreadName", "storeAll -after-: " + Thread.currentThread().getName());
    }

    @Override
    public void replaceAll(List<Announcement> modelList) {
        mCache.clear();
        storeAll(modelList);
    }

    @Override
    public Flowable<Announcement> getSingular(String s) {
        return null;
    }

    @Override
    public Flowable<List<Announcement>> getAll() {
        return mProcessor.startWith(new ArrayList<Announcement>(mCache.values()));
    }
}
