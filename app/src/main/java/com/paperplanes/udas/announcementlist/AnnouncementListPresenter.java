package com.paperplanes.udas.announcementlist;

import android.util.Log;

import com.paperplanes.udas.data.AnnouncementRepository;
import com.paperplanes.udas.model.Announcement;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by abdularis on 14/11/17.
 */

public class AnnouncementListPresenter {

    private AnnouncementListView mView;
    private AnnouncementRepository mRepo;
    private CompositeDisposable mCompositeDisposable;
    private boolean mFirstTime;

    @Inject
    public AnnouncementListPresenter(AnnouncementRepository repository) {
        mRepo = repository;
        mCompositeDisposable = new CompositeDisposable();
        mFirstTime = true;
    }

    public void refreshData() {
        mView.showLoading(true);
        mRepo.fetchAnnouncements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mView.showLoading(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mView.showLoading(false);
                        mView.showError(throwable.getMessage());
                    }
                });
    }

    public void setView(AnnouncementListView view) {
        mView = view;

        mCompositeDisposable.add(
                mRepo.getAllAnnouncement()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSubscriber<List<Announcement>>() {
                            @Override
                            public void onNext(List<Announcement> announcements) {
                                if (mFirstTime) {
                                    mFirstTime = false;
                                    refreshData();
                                }
                                else {
                                    mView.showLoading(false);
                                }
                                Log.v("ThreadName", "onNext: " + Thread.currentThread().getName() + " resp.size: " + announcements.size());
                                mView.showAnnouncementList(announcements);
                            }

                            @Override
                            public void onError(Throwable t) {
                                Log.v("ThreadName", "ERROR " + t);
                                mView.showError(t.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
    }

    public void retrieveData() {

    }

    public void dispose() {
        Log.v("RetrieveData", "disposing...");
        mCompositeDisposable.dispose();
    }

}
