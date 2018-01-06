package com.paperplanes.unma.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.common.exceptions.UnauthorizedNetworkException;
import com.paperplanes.unma.data.database.DatabaseAccess;
import com.paperplanes.unma.data.network.api.AnnouncementApi;
import com.paperplanes.unma.data.network.api.response.AnnouncementRespData;
import com.paperplanes.unma.data.pendingtask.MarkReadTask;
import com.paperplanes.unma.data.pendingtask.MarkReadTaskDatabase;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementRepository {
    private static final String TAG = AnnouncementRepository.class.getSimpleName();

    private MemoryReactiveStore<String, Announcement> mStore;
    private AnnouncementApi mAnnouncementService;
    private DatabaseAccess mDatabaseAccess;
    private MarkReadTaskDatabase mMarkReadTaskDatabase;
    private AnnouncementRepositoryPrefs mPrefs;

    public AnnouncementRepository(MemoryReactiveStore<String, Announcement> store,
                                  AnnouncementApi announcementService,
                                  DatabaseAccess databaseAccess,
                                  MarkReadTaskDatabase markReadTaskDatabase,
                                  AnnouncementRepositoryPrefs announcementRepositoryPrefs) {
        mStore = store;
        mAnnouncementService = announcementService;
        mDatabaseAccess = databaseAccess;
        mMarkReadTaskDatabase = markReadTaskDatabase;
        mPrefs = announcementRepositoryPrefs;
    }

    public void reset() {
        mPrefs.setSinceUpdated(0);
    }

    public void clearAll() {
        reset();

        mDatabaseAccess.clearAll();
        mMarkReadTaskDatabase.clearAll();
        mStore.clearAll();
    }

    public Completable markAsRead(Announcement announcement) {
        return mAnnouncementService.markAsRead(announcement.getId())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    announcement.setRead(true);
                    mDatabaseAccess.updateAnnouncementAsRead(announcement.getId());
                    mStore.notifySingularObserver(announcement.getId());
                    mStore.notifyObjectListObserver();
                })
                .doOnError(throwable -> {
                    Log.d(TAG, "Failed to mark announcement as read: " + throwable.toString());
                    mMarkReadTaskDatabase.insert(new MarkReadTask(announcement.getId()));
                })
                .andThen(processAllPendingTask());
    }

    public Single<Announcement> getDescriptionContent(@NonNull Announcement announcement) {
        return Single.just(announcement)
                .subscribeOn(Schedulers.computation())
                .flatMap(ann -> {
                    // query from local database
                    String content = mDatabaseAccess.getDescriptionContent(ann.getId());

                    if (content == null) {
                        // fetch from server if there is no content in local database
                        return mAnnouncementService
                                .downloadDescription(ann.getDescription().getUrl())
                                .map(responseBody -> {
                                    ann.getDescription().setContent(responseBody.string());
                                    ann.getDescription().setOffline(true);
                                    mDatabaseAccess.updateDescriptionContent(ann.getId(), ann.getDescription().getContent());
                                    mStore.notifySingularObserver(ann.getId());
                                    mStore.notifyObjectListObserver();
                                    return ann;
                                });
                    }

                    ann.getDescription().setContent(content);
                    return Single.just(ann);
                });
    }

    public Flowable<Optional<Announcement>> get(String id) {
        return mStore.getSingular(id)
                .subscribeOn(Schedulers.computation())
                .flatMap(announcementOptional -> {
                    if (announcementOptional.isNull())
                        return Flowable.just(Optional.of(mDatabaseAccess.getAnnouncement(id)));
                    return Flowable.just(Optional.of(announcementOptional.get()));
                })
                .doOnNext(announcementOptional -> verifyAttachmentAgainstExternalStorage(announcementOptional.get()));
    }

    public Completable fetch(String id) {
        Completable fetch = mAnnouncementService.getAnnouncement(id)
                .flatMap(resp -> {
                    if (resp.isSuccess() && resp.getData() != null)
                        return Single.just(resp.getData());
                    return Single.never();
                })
                .map(this::mapFromApiResponse)
                .doOnSuccess(announcement -> {
                    mDatabaseAccess.insert(announcement);
                    mStore.storeSingular(announcement);
                })
                .toCompletable();

        return processAllPendingTask()
                .andThen(fetch)
                .subscribeOn(Schedulers.io());
    }

    public Flowable<List<Announcement>> getAnnouncements() {
        return mStore.getAll()
                .subscribeOn(Schedulers.computation())
                .doOnNext(listOptional -> {
                    // if announcements is empty, try to query from local database
                    if (listOptional.isNotNull() && listOptional.get().isEmpty()) {
                        List<Announcement> announcementList = mDatabaseAccess.getAnnouncements();
                        if (!announcementList.isEmpty()) {
                            mStore.replaceAll(announcementList);
                        }
                    }
                })
                .flatMap(listOptional -> {
                    if (listOptional.isNotNull()) {
                        return Flowable.fromIterable(listOptional.get())
                                .doOnNext(this::verifyAttachmentAgainstExternalStorage)
                                .toList()
                                .toFlowable();
                    }
                    return Flowable.just(new ArrayList<>());
                });
    }

    public Completable fetchAnnouncements() {
        Completable fetch = mAnnouncementService
                .getAnnouncementList(mPrefs.getSinceUpdated() / 1000) // diserver seconds
                .toObservable()
                .flatMap(resp -> {
                    if (resp.isSuccess() && resp.getData() != null)
                        return Observable.fromIterable(resp.getData());
                    return Observable.empty();
                })
                .map(this::mapFromApiResponse)
                .toList()
                .doOnError(throwable -> {
                    if (throwable instanceof UnauthorizedNetworkException) {
                        clearAll();
                    }
                })
                .doOnSuccess(announcements -> {
                    Log.d(TAG, "Fetched data: " + announcements.size() + " items");

                    if (mPrefs.getSinceUpdated() == 0) {
                        mDatabaseAccess.clearAll();
                        mDatabaseAccess.insertAll(announcements);
                        mStore.replaceAll(announcements);
                    } else if (!announcements.isEmpty()) {
                        mDatabaseAccess.insertOrReplaceAll(announcements);
                        mStore.replaceAll(mDatabaseAccess.getAnnouncements());
                    }

                    mPrefs.setSinceUpdated(System.currentTimeMillis());
                })
                .toCompletable();

        return processAllPendingTask()
                .andThen(fetch)
                .subscribeOn(Schedulers.io());
    }

    public Completable processAllPendingTask() {
        List<MarkReadTask> tasks = mMarkReadTaskDatabase.getAll();
        if (tasks.size() > 0) {
            List<Completable> completableList = new ArrayList<>();
            for (MarkReadTask task : tasks) {
                completableList.add(mAnnouncementService.markAsRead(task.getAnnouncementId()));
            }

            return Completable
                    .merge(completableList)
                    .doOnComplete(() -> mMarkReadTaskDatabase.clearAll());
        }
        return Completable.complete();
    }

    private Announcement mapFromApiResponse(AnnouncementRespData resp) {
        Announcement announcement = new Announcement();
        announcement.setId(resp.getId());
        announcement.setTitle(resp.getTitle());
        announcement.setLastUpdated(new Date((long) (resp.getLastUpdated() * 1000L)));
        announcement.setPublisher(resp.getPublisher());
        announcement.setRead(resp.isRead());

        if (resp.getDescription() != null) {
            Description desc = new Description();
            desc.setUrl(resp.getDescription().getUrl());
            desc.setContent(resp.getDescription().getContent());
            desc.setSize(resp.getDescription().getSize());
            desc.setOffline(desc.getContent() != null);
            announcement.setDescription(desc);
        }

        if (resp.getAttachment() != null) {
            Attachment att = new Attachment();
            att.setUrl(resp.getAttachment().getUrl());
            att.setName(resp.getAttachment().getName());
            att.setMimeType(resp.getAttachment().getMimetype());
            att.setSize(resp.getAttachment().getSize());
            announcement.setAttachment(att);
        }

        verifyAttachmentAgainstExternalStorage(announcement);
        return announcement;
    }

    private void verifyAttachmentAgainstExternalStorage(@NonNull Announcement announcement) {
        Attachment attachment = announcement.getAttachment();

        if (attachment == null) return;
        if (attachment.getName() == null ||
                attachment.getName().isEmpty() ||
                attachment.getState() == Attachment.STATE_DOWNLOADING ||
                attachment.getState() == Attachment.STATE_DOWNLOAD_CONNECTING) return;

        String filepath = getAttachmentFilePath(announcement);
        if (FileUtil.isExists(filepath)) {
            attachment.setFilePath(filepath);
            attachment.setState(Attachment.STATE_OFFLINE);
            mDatabaseAccess.updateAttachmentFilePath(announcement.getId(), filepath);
        } else {
            attachment.setFilePath("");
            attachment.setState(Attachment.STATE_ONLINE);
            mDatabaseAccess.updateAttachmentFilePath(announcement.getId(), null);
        }
    }

    private String getAttachmentFilePath(Announcement announcement) {
        if (announcement.getAttachment() != null)
            return DownloadManager.ROOT_DOWNLOAD_DIR +
                    File.separator +
                    announcement.getId() +
                    File.separator +
                    announcement.getAttachment().getName();
        return "";
    }
}
