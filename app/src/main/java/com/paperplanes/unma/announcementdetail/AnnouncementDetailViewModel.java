package com.paperplanes.unma.announcementdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.common.SingleLiveEvent;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.data.DownloadManager;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Description;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by abdularis on 17/11/17.
 */

public class AnnouncementDetailViewModel extends ViewModel {

    private CompositeDisposable mCompositeDisposable;
    private AnnouncementRepository mRepo;
    private DownloadManager mDownloadManager;

    private MutableLiveData<Announcement> mAnnouncement;
    private SingleLiveEvent<Boolean> mLoadingDescription;
    private SingleLiveEvent<Description> mOnDescriptionLoaded;
    private String mCurrentAnnouncementId;

    private SingleLiveEvent<Throwable> mError;

    public AnnouncementDetailViewModel(AnnouncementRepository repo, DownloadManager downloadManager) {
        mRepo = repo;

        mAnnouncement = new MutableLiveData<>();
        mLoadingDescription = new SingleLiveEvent<>();
        mOnDescriptionLoaded = new SingleLiveEvent<>();
        mCurrentAnnouncementId = null;
        mDownloadManager = downloadManager;
        mError = new SingleLiveEvent<>();
    }

    @Override
    protected void onCleared() {
        if (mCompositeDisposable != null)
            mCompositeDisposable.dispose();
    }

    public void downloadAttachment() {
        Announcement announcement = mAnnouncement.getValue();
        if (announcement != null && announcement.getAttachment() != null) {
            mDownloadManager.startDownloadAttachment(announcement);
        }
    }

    private void loadDescriptionContent(Announcement announcement) {
        prepareCompositeDisposable();

        mCompositeDisposable.add(mRepo.getDescriptionContent(announcement)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Announcement>() {
                    @Override
                    public void onSuccess(Announcement description) {
                        mLoadingDescription.setValue(false);
                        mOnDescriptionLoaded.setValue(description.getDescription());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mLoadingDescription.setValue(false);
                        mError.setValue(throwable);
                    }
                }));
    }

    public void fetchFromServer(String announcementId) {
        prepareCompositeDisposable();

        mCompositeDisposable.add(mRepo.fetch(announcementId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mError.setValue(throwable);
                    }
                }));
    }

    public void loadCurrentAnnouncement() {
        if (mCurrentAnnouncementId == null) return;
        prepareCompositeDisposable();

        mCompositeDisposable.add(mRepo.get(mCurrentAnnouncementId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Optional<Announcement>>() {
                    @Override
                    public void onNext(Optional<Announcement> announcementOptional) {
                        if (announcementOptional.isNull()) {
                            fetchFromServer(mCurrentAnnouncementId);
                            return;
                        }

                        Announcement announcement = announcementOptional.get();
                        Description desc = announcement.getDescription();
                        if (desc != null) {
                            if (desc.getContent() == null) {
                                mLoadingDescription.setValue(true);
                                loadDescriptionContent(announcement);
                            } else {
                                mOnDescriptionLoaded.setValue(desc);
                            }
                        }
                        mAnnouncement.setValue(announcement);
                        markAnnouncementAsRead(announcement);
                    }

                    @Override
                    public void onError(Throwable t) {
                        mError.setValue(t);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void markAnnouncementAsRead(Announcement announcement) {
        if (!announcement.isRead())
            mRepo.markAsRead(announcement)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {}

                        @Override
                        public void onError(Throwable throwable) {
                            mError.setValue(throwable);
                        }
                    });
    }

    public void setCurrentAnnouncementId(String currentAnnouncementId) {
        mCurrentAnnouncementId = currentAnnouncementId;
        loadCurrentAnnouncement();
    }

    private void prepareCompositeDisposable() {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
    }

    public DownloadManager getDownloadManager() {
        return mDownloadManager;
    }

    public MutableLiveData<Announcement> getAnnouncement() {
        return mAnnouncement;
    }

    public MutableLiveData<Boolean> getLoadingDescription() {
        return mLoadingDescription;
    }

    public SingleLiveEvent<Description> getOnDescriptionLoaded() {
        return mOnDescriptionLoaded;
    }

    public SingleLiveEvent<Throwable> getError() {
        return mError;
    }

}
