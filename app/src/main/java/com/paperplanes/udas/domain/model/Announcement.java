package com.paperplanes.udas.domain.model;

import java.util.Date;

/**
 * Created by abdularis on 08/11/17.
 */

public class Announcement {

    private String mId;
    private String mTitle;
    private Description mDescription;
    private Date mLastUpdated;
    private Attachment mAttachment;
    private boolean mRead;

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

    public Description getDescription() {
        return mDescription;
    }

    public void setDescription(Description description) {
        mDescription = description;
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
