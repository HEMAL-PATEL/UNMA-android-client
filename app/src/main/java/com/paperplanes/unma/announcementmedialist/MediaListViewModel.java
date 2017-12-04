package com.paperplanes.unma.announcementmedialist;

import com.paperplanes.unma.announcementlist.AnnouncementListViewModel;
import com.paperplanes.unma.auth.SessionManager;
import com.paperplanes.unma.data.AnnouncementRepository;
import com.paperplanes.unma.data.DownloadManager;
import com.paperplanes.unma.model.Announcement;

import io.reactivex.functions.Predicate;

/**
 * Created by abdularis on 01/12/17.
 */

public class MediaListViewModel extends AnnouncementListViewModel {

    private static final FilterAttachment sDefFilter = new FilterAttachment();
    private DownloadManager mDownloadManager;

    public MediaListViewModel(AnnouncementRepository repository,
                              SessionManager sessionManager,
                              DownloadManager downloadManager) {
        super(repository, sessionManager);
        mDownloadManager = downloadManager;
    }

    @Override
    public Predicate<Announcement> getDefaultFilter() {
        return sDefFilter;
    }

    public void downloadAttachment(Announcement announcement) {
        if (announcement == null) return;
        mDownloadManager.startDownloadAttachment(announcement);
    }

    public DownloadManager getDownloadManager() {
        return mDownloadManager;
    }
}
