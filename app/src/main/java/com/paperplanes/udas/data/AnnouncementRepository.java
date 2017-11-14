package com.paperplanes.udas.data;

import android.util.Log;

import com.paperplanes.udas.data.network.api.AnnouncementApi;
import com.paperplanes.udas.model.Announcement;
import com.paperplanes.udas.model.Attachment;
import com.paperplanes.udas.model.Description;

import java.net.URL;
import java.util.Date;
import java.util.List;

import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementRepository {

    private ReactiveStore.AnnouncementStore mStore;
    private AnnouncementApi mAnnouncementService;

    public AnnouncementRepository(ReactiveStore.AnnouncementStore store,
                                  AnnouncementApi announcementService) {
        mStore = store;
        mAnnouncementService = announcementService;
    }

    public Flowable<List<Announcement>> getAllAnnouncement() {
        return mStore.getAll();
    }

    public Completable fetchAnnouncements() {
        return mAnnouncementService
                .getAnnouncementList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .flatMap(resp -> {
                    Log.v("ThreadName","repo: " + Thread.currentThread().getName());
                    if (resp.isSuccess() && resp.getData() != null)
                        return Observable.fromIterable(resp.getData());
                    return Observable.empty();
                })
                .map(resp -> {
                    Announcement announcement = new Announcement();
                    announcement.setId(resp.getId());
                    announcement.setTitle(resp.getTitle());
                    announcement.setLastUpdated(new Date((long) (resp.getLastUpdated() * 1000L)));
                    announcement.setPublisher(resp.getPublisher());
                    announcement.setRead(resp.isRead());

                    if (resp.getDescription() != null) {
                        Description desc = new Description();
                        desc.setUrl(new URL(resp.getDescription().getUrl()));
                        desc.setContent(resp.getDescription().getContent());
                        desc.setSize(resp.getDescription().getSize());
                        announcement.setDescription(desc);
                    }

                    if (resp.getAttachment() != null) {
                        Attachment att = new Attachment();
                        att.setUrl(new URL(resp.getAttachment().getUrl()));
                        att.setName(resp.getAttachment().getName());
                        att.setMimeType(resp.getAttachment().getMimetype());
                        att.setSize(resp.getAttachment().getSize());
                        announcement.setAttachment(att);
                    }

                    return announcement;
                })
                .toList()
                .doOnSuccess(announcements -> mStore.replaceAll(announcements))
                .toCompletable();
    }

}
