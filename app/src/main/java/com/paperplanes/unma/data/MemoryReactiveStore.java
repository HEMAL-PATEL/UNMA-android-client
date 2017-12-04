package com.paperplanes.unma.data;

import com.paperplanes.unma.common.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by abdularis on 24/11/17.
 */

public class MemoryReactiveStore <Key, Value> implements ReactiveStore<Key, Value> {

    private FlowableProcessor<Optional<List<Value>>> mProcessorAll;
    private HashMap<Key, FlowableProcessor<Optional<Value>>> mProcessorSingularMap;

    private HashMap<Key, Value> mMapCache;
    private List<Value> mCacheList;

    private Function<Value, Key> mModelKeyExtractor;

    public MemoryReactiveStore(Function<Value, Key> modelKeyExtractor) {
        mProcessorAll = PublishProcessor.create();
        mProcessorSingularMap = new HashMap<>();
        mMapCache = new HashMap<>();
        mCacheList = new ArrayList<>();
        mModelKeyExtractor = modelKeyExtractor;
    }

    @Override
    public void storeSingular(Value model) throws Exception {
        Key key = mModelKeyExtractor.apply(model);
        mMapCache.put(key, model);

        mCacheList.clear();
        mCacheList.addAll(mMapCache.values());

        notifySingularObserver(key);
        mProcessorAll.onNext(Optional.of(mCacheList));
    }

    @Override
    public void storeAll(List<Value> modelList) throws Exception {
        for (Value val : modelList)
            mMapCache.put(mModelKeyExtractor.apply(val), val);

        mCacheList.clear();
        mCacheList.addAll(mMapCache.values());

        notifySingularObservers();
        mProcessorAll.onNext(Optional.of(mCacheList));
    }

    @Override
    public void replaceAll(List<Value> modelList) throws Exception {
        mCacheList.clear();
        mMapCache.clear();

        storeAll(modelList);
    }

    @Override
    public Flowable<Optional<Value>> getSingular(Key key) {
        FlowableProcessor<Optional<Value>> processor = mProcessorSingularMap.get(key);
        if (processor == null)
            processor = createProcessorForSingularKey(key);
        return processor.startWith(Optional.of(mMapCache.get(key)));
    }

    @Override
    public Flowable<Optional<List<Value>>> getAll() {
        return mProcessorAll.startWith(Optional.of(mCacheList));
    }

    @Override
    public void clearAll() {
        mCacheList.clear();
        mMapCache.clear();
        notifySingularObservers();
        mProcessorAll.onNext(Optional.of(null));
        mProcessorSingularMap.clear();
    }

    private FlowableProcessor<Optional<Value>> createProcessorForSingularKey(Key key) {
        FlowableProcessor<Optional<Value>> processor = PublishProcessor.create();
        mProcessorSingularMap.put(key, processor);
        return processor;
    }

    public void notifySingularObserver(Key key) {
        FlowableProcessor<Optional<Value>> processor = mProcessorSingularMap.get(key);
        if (processor != null) {
            Value val = mMapCache.get(key);
            if (val != null) {
                processor.onNext(Optional.of(val));
            }
            else {
                processor.onComplete();
                mProcessorSingularMap.remove(key);
            }
        }
    }

    public void notifySingularObservers() {
        List<Key> toBeRemoved = new ArrayList<>();
        for (Key key : mProcessorSingularMap.keySet()) {
            Value val = mMapCache.get(key);
            if (val != null)
                mProcessorSingularMap.get(key).onNext(Optional.of(val));
            else
                toBeRemoved.add(key);
        }

        for (Key key : toBeRemoved)
            mProcessorSingularMap.remove(key).onComplete();
    }

    public void notifyObjectListObserver() {
        mProcessorAll.onNext(Optional.of(mCacheList));
    }
}
