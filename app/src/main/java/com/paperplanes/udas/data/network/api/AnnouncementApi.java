package com.paperplanes.udas.data.network.api;

import com.paperplanes.udas.data.network.api.response.AnnouncementRespData;
import com.paperplanes.udas.data.network.api.response.JsonResp;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by abdularis on 08/11/17.
 */

public interface AnnouncementApi {

    @GET("announcements")
    Single<JsonResp<List<AnnouncementRespData>>>
    getAnnouncementList();

    @GET
    Single<JsonResp<String>>
    getDescription(@Url String url);

    @Streaming
    @GET
    Single<ResponseBody>
    downloadAttachment(@Url String url);

}
