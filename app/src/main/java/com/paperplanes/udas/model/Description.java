package com.paperplanes.udas.model;

import java.net.URL;

/**
 * Created by abdularis on 08/11/17.
 */

public class Description {

    private URL mUrl;
    private String mContent;
    private long mSize;

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL url) {
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
