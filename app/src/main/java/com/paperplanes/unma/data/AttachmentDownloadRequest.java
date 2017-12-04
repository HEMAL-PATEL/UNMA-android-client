package com.paperplanes.unma.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abdularis on 03/12/17.
 */

public class AttachmentDownloadRequest implements Parcelable {

    public static final Creator<AttachmentDownloadRequest> CREATOR =
            new Creator<AttachmentDownloadRequest>() {
                @Override
                public AttachmentDownloadRequest createFromParcel(Parcel in) {
                    return new AttachmentDownloadRequest(in);
                }

                @Override
                public AttachmentDownloadRequest[] newArray(int size) {
                    return new AttachmentDownloadRequest[size];
                }
            };

    private String mAnnouncementId;
    private String mAttachmentName;
    private String mAttachmentUrl;

    protected AttachmentDownloadRequest(Parcel in) {
        this.mAnnouncementId = in.readString();
        this.mAttachmentName = in.readString();
        this.mAttachmentUrl = in.readString();
    }

    public AttachmentDownloadRequest(String announcementId,
                                     String attachmentName,
                                     String attachmentUrl) {
        mAnnouncementId = announcementId;
        mAttachmentName = attachmentName;
        mAttachmentUrl = attachmentUrl;
    }

    public String getAnnouncementId() {
        return mAnnouncementId;
    }

    public String getAttachmentName() {
        return mAttachmentName;
    }

    public String getAttachmentUrl() {
        return mAttachmentUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAnnouncementId);
        dest.writeString(mAttachmentName);
        dest.writeString(mAttachmentUrl);
    }
}
