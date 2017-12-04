package com.paperplanes.unma.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.common.Optional;
import com.paperplanes.unma.common.exceptions.UnauthorizedNetworkException;
import com.paperplanes.unma.data.database.DatabaseAccess;
import com.paperplanes.unma.data.network.api.AnnouncementApi;
import com.paperplanes.unma.data.network.api.response.AnnouncementRespData;
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
    private AnnouncementRepositoryPrefs mPrefs;

    public AnnouncementRepository(MemoryReactiveStore<String, Announcement> store,
                                  AnnouncementApi announcementService,
                                  DatabaseAccess databaseAccess,
                                  AnnouncementRepositoryPrefs announcementRepositoryPrefs) {
        mStore = store;
        mAnnouncementService = announcementService;
        mDatabaseAccess = databaseAccess;
        mPrefs = announcementRepositoryPrefs;
    }

    public void clearAll() {
        mPrefs.setSinceUpdated(0);

        mDatabaseAccess.clearAll();
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
                    // TODO save the task and try again later
                });
    }

    public Single<Announcement> getDescriptionContent(@NonNull Announcement announcement) {
        return Single.just(announcement)
                .subscribeOn(Schedulers.io())
                .flatMap(ann -> {
                    // query from local database
                    String content = mDatabaseAccess.getDescriptionContent(ann.getId());

                    if (content == null) {
                        // fetch from server if there is no content in local database
                        return mAnnouncementService
                                .downloadDescription(ann.getDescription().getUrl())
                                .map(responseBody -> {
                                    ann.getDescription().setContent(responseBody.string());
                                    mDatabaseAccess.updateDescriptionContent(ann.getId(), ann.getDescription().getContent());
                                    return ann;
                                });
                    }

                    ann.getDescription().setContent(content);
                    return Single.just(ann);
                });
    }

//    public Observable<Integer> downloadAttachment(Announcement announcement) {
//        String announcementId = announcement.getId();
//        if (mPublishSubjectMap.containsKey(announcementId)) {
//            return mPublishSubjectMap.get(announcementId);
//        }
//
//        Attachment attachment = announcement.getAttachment();
//        PublishSubject<Integer> publishSubject = PublishSubject.create();
//        mPublishSubjectMap.put(announcementId, publishSubject);
//
//        mAnnouncementService.downloadAttachment(attachment.getUrl())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new DisposableSingleObserver<ResponseBody>() {
//                    @Override
//                    public void onSuccess(ResponseBody resp) {
//                        if (!FileUtil.isExternalStorageAvailable())
//                            throw new ExternalStorageUnavailableException();
//
//                        String folder = mPrefs.getAttachmentDownloadFolder() + File.separator + announcementId;
//                        File dir = new File(folder);
//                        dir.mkdirs();
//                        File file = new File(dir, attachment.getName() + ".temp");
//
//                        attachment.setState(Attachment.STATE_DOWNLOADING);
//                        InputStream inputStream;
//                        OutputStream outputStream;
//                        try {
//                            byte[] buffer = new byte[4096];
//
//                            inputStream = resp.byteStream();
//                            outputStream = new FileOutputStream(file);
//
//                            long fileSize = resp.contentLength();
//                            long downloaded = 0;
//
//                            long startTime = System.currentTimeMillis();
//                            long ellapsedTime;
//                            int read;
//                            while ((read = inputStream.read(buffer)) > 0) {
//                                outputStream.write(buffer, 0, read);
//                                downloaded += read;
//
//                                ellapsedTime = System.currentTimeMillis() - startTime;
//                                if (ellapsedTime >= 1000L) {
//                                    startTime = System.currentTimeMillis();
//
//                                    publishSubject.onNext((int) (downloaded * 100 / fileSize));
//                                }
//                            }
//                            publishSubject.onNext((int) (downloaded * 100 / fileSize));
//
//                            outputStream.flush();
//                            file.renameTo(new File(folder, attachment.getName()));
//                            attachment.setFilePath(file.getAbsolutePath());
//                            attachment.setState(Attachment.STATE_OFFLINE);
//                            mDatabaseAccess.updateAttachmentFilePath(announcementId, attachment.getFilePath());
//                            publishSubject.onComplete();
//                            mPublishSubjectMap.remove(announcementId);
//
//                        } catch (Exception e) {
//                            publishSubject.onError(e);
//
//                            attachment.setState(Attachment.STATE_ONLINE);
//                            mPublishSubjectMap.remove(announcementId);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        publishSubject.onError(throwable);
//                        mPublishSubjectMap.remove(announcementId);
//                    }
//                });
//        return publishSubject;
//    }

    public Flowable<Optional<Announcement>> get(String id) {
        return mStore.getSingular(id)
                .subscribeOn(Schedulers.io())
                .flatMap(announcementOptional -> {
                    if (announcementOptional.isNull())
                        return Flowable.just(Optional.of(mDatabaseAccess.getAnnouncement(id)));
                    return Flowable.just(Optional.of(announcementOptional.get()));
                })
                .doOnNext(announcementOptional -> verifyAttachmentAgainstExternalStorage(announcementOptional.get()));
    }

    public Completable fetch(String id) {
        return mAnnouncementService.getAnnouncement(id)
                .subscribeOn(Schedulers.io())
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
    }

    public Flowable<List<Announcement>> getAnnouncements() {
        return mStore.getAll()
                .subscribeOn(Schedulers.io())
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
//                    return Flowable.empty();
                    return Flowable.just(new ArrayList<>());
                });
    }

    public Completable fetchAnnouncements() {
        return mAnnouncementService
                .getAnnouncementList(mPrefs.getSinceUpdated() / 1000) // diserver seconds
                .toObservable()
                .subscribeOn(Schedulers.io())
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
    }


    private Announcement mapFromApiResponse(AnnouncementRespData resp) {
        Announcement announcement = new Announcement();
        announcement.setId(resp.getId());
        announcement.setTitle(resp.getTitle());
        announcement.setThumbnailUrl(resp.getThumbnailUrl());
        announcement.setLastUpdated(new Date((long) (resp.getLastUpdated() * 1000L)));
        announcement.setPublisher(resp.getPublisher());
        announcement.setRead(resp.isRead());

        if (resp.getDescription() != null) {
            Description desc = new Description();
            desc.setUrl(resp.getDescription().getUrl());
            desc.setContent(resp.getDescription().getContent());
            desc.setSize(resp.getDescription().getSize());
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
                attachment.getState() == Attachment.STATE_DOWNLOADING) return;

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
            return mPrefs.getAttachmentDownloadFolder() +
                    File.separator +
                    announcement.getId() +
                    File.separator +
                    announcement.getAttachment().getName();
        return "";
    }
}