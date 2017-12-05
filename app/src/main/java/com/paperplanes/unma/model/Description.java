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

    public Description() {
        this(null, "", 0);
    }

    public Description(String url, String content, long size) {
        mUrl = url;
        mContent = content;
        mSize = size;
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
}
