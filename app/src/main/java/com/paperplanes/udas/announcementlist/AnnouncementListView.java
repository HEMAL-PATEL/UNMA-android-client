package com.paperplanes.udas.announcementlist;

import com.paperplanes.udas.model.Announcement;

import java.util.List;

/**
 * Created by abdularis on 14/11/17.
 */

public interface AnnouncementListView {
    void showLoading(boolean active);

    void showAnnouncementList(List<Announcement> announcements);

    void showError(String errMsg);
}
