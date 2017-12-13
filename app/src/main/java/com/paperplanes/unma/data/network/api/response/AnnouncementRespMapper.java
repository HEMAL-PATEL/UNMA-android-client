package com.paperplanes.unma.data.network.api.response;

import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;
import com.paperplanes.unma.model.Description;

import java.util.Date;

import io.reactivex.functions.Function;

/**
 * Created by abdularis on 25/11/17.
 */

public class AnnouncementRespMapper implements Function<AnnouncementRespData, Announcement> {
    @Override
    public Announcement apply(AnnouncementRespData resp) throws Exception {
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

        return announcement;
    }
}
