package com.paperplanes.udas.data;

import com.paperplanes.udas.data.api.AnnouncementApi;
import com.paperplanes.udas.data.api.response.AnnouncementRespData;
import com.paperplanes.udas.data.api.response.JsonResp;
import com.paperplanes.udas.domain.SessionManager;
import com.paperplanes.udas.domain.data.AnnouncementDataSource;
import com.paperplanes.udas.domain.model.Announcement;
import com.paperplanes.udas.domain.model.Attachment;
import com.paperplanes.udas.domain.model.Description;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;

/**
 * Created by abdularis on 08/11/17.
 */

public class AnnouncementRepository implements AnnouncementDataSource {

    private AnnouncementApi mApi;
    private SessionManager mSessionManager;

    @Inject
    public AnnouncementRepository(AnnouncementApi announcementApi, SessionManager sessionManager) {
        mApi = announcementApi;
        mSessionManager = sessionManager;
    }

    @Override
    public Observable<List<Announcement>> getAnnouncementList() {
        if (!mSessionManager.isSessionSet()) {
            return Observable.just(new ArrayList<>());
        }

        return mApi.getAnnouncementList("key=" + mSessionManager.getSession().getAccessToken())
                .toObservable()
                .flatMap(new Function<JsonResp<List<AnnouncementRespData>>, Observable<AnnouncementRespData>>() {
                    @Override
                    public Observable<AnnouncementRespData> apply(JsonResp<List<AnnouncementRespData>> listJsonResp) throws Exception {
                        if (listJsonResp.isSuccess()) {
                            return Observable.fromIterable(listJsonResp.getData());
                        }
                        return Observable.empty();
                    }
                })
                .map(new Function<AnnouncementRespData, Announcement>() {
                    @Override
                    public Announcement apply(AnnouncementRespData resp) throws Exception {
                        Announcement announcement = new Announcement();
                        announcement.setId(resp.getId());
                        announcement.setTitle(resp.getTitle());
                        announcement.setLastUpdated(new Date(new Timestamp((long)resp.getLastUpdated()).getTime()));
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
                    }
                })
                .toList()
                .toObservable();
    }

    @Override
    public Completable fetchAnnouncements() {
        return null;
    }
}
