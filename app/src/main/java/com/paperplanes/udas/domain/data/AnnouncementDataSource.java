package com.paperplanes.udas.domain.data;

import com.paperplanes.udas.domain.model.Announcement;
import com.paperplanes.udas.domain.model.Description;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by abdularis on 08/11/17.
 */

public interface AnnouncementDataSource {

    Observable<List<Announcement>> getAnnouncementList();

    Completable fetchAnnouncements();

}
