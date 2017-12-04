package com.paperplanes.unma.data.network.api;

import com.paperplanes.unma.data.network.api.response.AnnouncementRespData;
import com.paperplanes.unma.data.network.api.response.JsonResp;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by abdularis on 08/11/17.
 */

public interface AnnouncementApi {

    @GET("announcements")
    Single<JsonResp<List<AnnouncementRespData>>> getAnnouncementList(@Query("since") long since);

    @GET("announcements/{anc_id}")
    Single<JsonResp<AnnouncementRespData>> getAnnouncement(@Path("anc_id") String announcementId);

    @PUT("announcements/{anc_id}/read")
    Completable markAsRead(@Path("anc_id") String anc_id);

    @Streaming
    @GET
    Single<ResponseBody> downloadDescription(@Url String url);

    @Streaming
    @GET
    Single<ResponseBody> downloadAttachment(@Url String url);

}
