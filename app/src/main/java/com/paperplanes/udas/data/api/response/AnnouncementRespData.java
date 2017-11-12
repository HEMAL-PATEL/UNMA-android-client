package com.paperplanes.udas.data.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abdularis on 08/11/17.
 */

public class AnnouncementRespData {

    @SerializedName("id") String mId;
    @SerializedName("title") String mTitle;
    @SerializedName("description") Description mDescription;
    @SerializedName("publisher") String mPublisher;
    @SerializedName("last_updated") double mLastUpdated;
    @SerializedName("attachment") Attachment mAttachment;
    @SerializedName("read") boolean mRead;

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Description getDescription() {
        return mDescription;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public double getLastUpdated() {
        return mLastUpdated;
    }

    public Attachment getAttachment() {
        return mAttachment;
    }

    public boolean isRead() {
        return mRead;
    }

    public static class Description {
        String url;
        String content;
        long size;

        public String getUrl() {
            return url;
        }

        public String getContent() {
            return content;
        }

        public long getSize() {
            return size;
        }
    }

    public static class Attachment {
        String url;
        String name;
        String mimetype;
        long size;

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getMimetype() {
            return mimetype;
        }

        public long getSize() {
            return size;
        }
    }
}
