package com.paperplanes.unma.data.pendingtask;

/**
 * Created by abdularis on 04/12/17.
 */

public class MarkReadTask {
    private String mAnnouncementId;

    public MarkReadTask(String announcementId) {
        mAnnouncementId = announcementId;
    }

    public String getAnnouncementId() {
        return mAnnouncementId;
    }
}
