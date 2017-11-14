package com.paperplanes.udas.model;

import java.net.URL;

/**
 * Created by abdularis on 08/11/17.
 */

public class Attachment {

    private URL mUrl;
    private String mFilePath;
    private String mName;
    private String mMimeType;
    private long mSize;

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL url) {
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
