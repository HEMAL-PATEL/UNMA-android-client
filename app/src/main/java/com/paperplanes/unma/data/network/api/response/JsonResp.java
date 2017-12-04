package com.paperplanes.unma.data.network.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by abdularis on 31/10/17.
 */

public class JsonResp <DataType> {

    @SerializedName("success") boolean mSuccess;
    @SerializedName("message") String mMessage;
    @SerializedName("data") DataType mData;

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public DataType getData() {
        return mData;
    }
}
