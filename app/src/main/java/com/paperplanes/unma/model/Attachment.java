package com.paperplanes.unma.model;

/**
 * Created by abdularis on 08/11/17.
 */

public class Attachment {

    public static final int STATE_OFFLINE = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_ONLINE = 3;
    public static final int STATE_DOWNLOAD_CONNECTING = 4;

    private String mUrl;
    private String mFilePath;
    private String mName;
    private String mMimeType;
    private long mSize;
    private int mState;

    public Attachment() {
        this(null, "", "", "", 0);
    }

    public Attachment(String url, String filePath, String name, String mimeType, long size) {
        mUrl = url;
        mFilePath = filePath;
        mName = name;
        mMimeType = mimeType;
        mSize = size;
    }

    public void setState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

}
