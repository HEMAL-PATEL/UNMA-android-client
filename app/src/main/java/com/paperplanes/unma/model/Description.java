package com.paperplanes.unma.model;


/**
 * Created by abdularis on 08/11/17.
 *
 * Deskripsi pengumuman/konten
 */

public class Description {

    private String mUrl;
    private String mContent;
    private long mSize;
    private boolean mOffline;

    public Description() {
        this(null, "", 0, false);
    }

    public Description(String url, String content, long size, boolean offline) {
        mUrl = url;
        mContent = content;
        mSize = size;
        mOffline = offline;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public boolean isOffline() {
        return mOffline;
    }

    public void setOffline(boolean offline) {
        mOffline = offline;
    }
}
