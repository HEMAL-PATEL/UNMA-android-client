package com.paperplanes.unma.model;

import java.util.Date;

/**
 * Created by abdularis on 08/11/17.
 *
 * Represent announcement, it's a core model
 */

public class Announcement {

    private String mId;
    private String mTitle;
    private String mThumbnailUrl;
    private Description mDescription;
    private String mPublisher;
    private Date mLastUpdated;
    private Attachment mAttachment;
    private boolean mRead;

    public Announcement() {
    }

    public Announcement(String id,
                        String title,
                        String thumbnailUrl,
                        Description description,
                        String publisher,
                        Date lastUpdated,
                        Attachment attachment,
                        boolean read) {
        mId = id;
        mTitle = title;
        mThumbnailUrl = thumbnailUrl;
        mDescription = description;
        mPublisher = publisher;
        mLastUpdated = lastUpdated;
        mAttachment = attachment;
        mRead = read;
    }

    public long getTotalSize() {
        long size = 0;
        if (mDescription != null) {
            size += mDescription.getSize();
        }
        if (mAttachment != null) {
            size += mAttachment.getSize();
        }
        return size;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public Description getDescription() {
        return mDescription;
    }

    public void setDescription(Description description) {
        mDescription = description;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String publisher) {
        mPublisher = publisher;
    }

    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        mLastUpdated = lastUpdated;
    }

    public Attachment getAttachment() {
        return mAttachment;
    }

    public void setAttachment(Attachment attachment) {
        mAttachment = attachment;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }
}
