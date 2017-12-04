package com.paperplanes.unma.announcementlist;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.model.Announcement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by abdularis on 16/11/17.
 */

public class AnnouncementListViewModel extends ViewModel {

    private AnnouncementRepository mRepo;
    private CompositeDisposable mCompositeDisposable;
    private boolean mShouldRefresh;
    private Predicate<Announcement> mFilter;
    private int mFilterId;

    private SingleLiveEvent<Boolean> mLoading;
    private SingleLiveEvent<Throwable> mError;
    private MutableLiveData<List<Announcement>> mAnnouncements;

    @Inject
    public AnnouncementListViewModel(AnnouncementRepository repository,
                                     SessionManager sessionManager) {
        mRepo = repository;

        mLoading = new SingleLiveEvent<>();
        mError = new SingleLiveEvent<>();
        mAnnouncements = new MutableLiveData<>();
        mShouldRefresh = true;
        mFilter = getDefaultFilter();
        sessionManager
                .observeOnLogout(
                        AndroidSchedulers.mainThread(),
                        event -> {
                            dispose();
                            mFilter = getDefaultFilter();
                            mFilterId = 0;
                            mShouldRefresh = true;
                            mAnnouncements.setValue(new ArrayList<>());
                        });
    }

    @Override
    protected void onCleared() {
        dispose();
    }

    public Predicate<Announcement> getDefaultFilter() {
        return announcement -> true;
    }

    private void dispose() {
        if (mCompositeDisposable != null)
            mCompositeDisposable.dispose();
    }

    public void forceRefresh() {
        mLoading.setValue(true);
        mRepo.reset();
        refresh();
    }

    public void refresh() {
        prepareCompositeDisposable();
        mCompositeDisposable.add(mRepo.fetchAnnouncements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLoading.setValue(false);
                        mError.setValue(throwable);
                    }
                }));
    }

    public void setFilter(Predicate<Announcement> filterPredicate, int filterId) {
        if (filterPredicate != null) {
            mFilter = filterPredicate;
            mFilterId = filterId;
        }
    }

    public void reSubscribeToData() {
        dispose();
        startListenToData();
    }

    public void startListenToData() {
        prepareCompositeDisposable();
        mLoading.setValue(true);
        mCompositeDisposable.add(
                mRepo.getAnnouncements()
                        .flatMap(announcements -> {
                            Collections.sort(announcements, (o1, o2) -> (int) (o2.getLastUpdated().getTime() - o1.getLastUpdated().getTime()));
                            return Flowable.fromIterable(announcements)
                                    .filter(mFilter)
                                    .toList()
                                    .toFlowable();
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSubscriber<List<Announcement>>() {
                            @Override
                            public void onNext(List<Announcement> announcements) {
                                if (mShouldRefresh) {
                                    mShouldRefresh = false;
                                    refresh();
                                } else {
                                    mLoading.setValue(false);
                                }

                                mAnnouncements.setValue(announcements);
                            }

                            @Override
                            public void onError(Throwable t) {
                                mLoading.setValue(false);
                                mError.setValue(t);
                            }

                            @Override
                            public void onComplete() {
                                // never complete coy!
                            }
                        }));
    }

    private void prepareCompositeDisposable() {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
    }

    public MutableLiveData<Boolean> getLoading() {
        return mLoading;
    }

    public MutableLiveData<Throwable> getError() {
        return mError;
    }

    public MutableLiveData<List<Announcement>> getAnnouncements() {
        return mAnnouncements;
    }

    public int getFilterId() {
        return mFilterId;
    }
}
