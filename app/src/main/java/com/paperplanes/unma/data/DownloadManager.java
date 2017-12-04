package com.paperplanes.unma.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.paperplanes.unma.data.database.DatabaseAccess;
import com.paperplanes.unma.model.Announcement;
import com.paperplanes.unma.model.Attachment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by abdularis on 29/11/17.
 */

public class DownloadManager {

    public static final String ROOT_DOWNLOAD_DIR =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "UnmaApp";

    private List<DownloadEventListener> mListenerList;
    private HashMap<String, Announcement> mAnnouncementMap;
    private DatabaseAccess mDatabaseAccess;
    private Context mContext;

    @Inject
    public DownloadManager(Context context, DatabaseAccess databaseAccess) {
        mContext = context;
        mListenerList = new ArrayList<>();
        mAnnouncementMap = new HashMap<>();
        mDatabaseAccess = databaseAccess;
        registerBroadcastReceiver();
    }

    public void startDownloadAttachment(@NonNull Announcement announcement) {
        String announcementId = announcement.getId();
        if (mAnnouncementMap.containsKey(announcementId)) return;
        if (announcement.getAttachment() == null) return;

        mAnnouncementMap.put(announcementId, announcement);
        AttachmentDownloadRequest request =
                new AttachmentDownloadRequest(announcementId,
                        announcement.getAttachment().getName(),
                        announcement.getAttachment().getUrl());

        Intent intent = new Intent(mContext, DownloadManagerService.class);
        intent.setAction(DownloadManagerService.ACTION_DOWNLOAD_ATTACHMENT);
        intent.putExtra(DownloadManagerService.EXTRA_DOWNLOAD_REQUEST, request);
        mContext.startService(intent);
    }

    public void addDownloadEventListener(DownloadEventListener listener) {
        mListenerList.add(listener);
    }

    public void removeDownloadEventListener(DownloadEventListener listener) {
        mListenerList.remove(listener);
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManagerService.ACTION_DOWNLOAD_STARTED);
        intentFilter.addAction(DownloadManagerService.ACTION_DOWNLOAD_PROGRESSED);
        intentFilter.addAction(DownloadManagerService.ACTION_DOWNLOAD_FAILED);
        intentFilter.addAction(DownloadManagerService.ACTION_DOWNLOAD_FINISHED);

        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(new DownloadBroadcastReceiver(), intentFilter);
    }

    private void callDownloadStarted(Announcement announcement) {
        for (DownloadEventListener listener : mListenerList) listener.onDownloadStarted(announcement);
    }

    private void callDownloadProgressed(Announcement announcement, int progress) {
        for (DownloadEventListener listener : mListenerList) listener.onDownloadProgressed(announcement, progress);
    }

    private void callDownloadFailed(Announcement announcement) {
        for (DownloadEventListener listener : mListenerList) listener.onDownloadFailed(announcement);
    }

    private void callDownloadFinished(Announcement announcement) {
        for (DownloadEventListener listener : mListenerList) listener.onDownloadFinished(announcement);
    }

    private class DownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (intent.getAction() == null) return;

            String action = intent.getAction();
            switch (action) {
                case DownloadManagerService.ACTION_DOWNLOAD_STARTED:
                    handleDownloadStarted(intent);
                    break;
                case DownloadManagerService.ACTION_DOWNLOAD_PROGRESSED:
                    handleDownloadProgressed(intent);
                    break;
                case DownloadManagerService.ACTION_DOWNLOAD_FAILED:
                    handleDownloadFailed(intent);
                    break;
                case DownloadManagerService.ACTION_DOWNLOAD_FINISHED:
                    handleDownloadFinished(intent);
                    break;
            }
        }

        private void handleDownloadStarted(Intent intent) {
            String annId = intent.getStringExtra(DownloadManagerService.EXTRA_ANNOUNCEMENT_ID);
            Announcement announcement = mAnnouncementMap.get(annId);
            announcement.getAttachment().setState(Attachment.STATE_DOWNLOADING);
            callDownloadStarted(mAnnouncementMap.get(annId));
        }

        private void handleDownloadProgressed(Intent intent) {
            String annId = intent.getStringExtra(DownloadManagerService.EXTRA_ANNOUNCEMENT_ID);
            int progress = intent.getIntExtra(DownloadManagerService.EXTRA_DOWNLOAD_PROGRESS, 0);
            callDownloadProgressed(mAnnouncementMap.get(annId), progress);
        }

        private void handleDownloadFailed(Intent intent) {
            String annId = intent.getStringExtra(DownloadManagerService.EXTRA_ANNOUNCEMENT_ID);
            Announcement announcement = mAnnouncementMap.get(annId);
            announcement.getAttachment().setState(Attachment.STATE_ONLINE);
            mAnnouncementMap.remove(annId);
            callDownloadFailed(announcement);
        }

        private void handleDownloadFinished(Intent intent) {
            String annId = intent.getStringExtra(DownloadManagerService.EXTRA_ANNOUNCEMENT_ID);
            String filepath = intent.getStringExtra(DownloadManagerService.EXTRA_DOWNLOAD_FILE_PATH);

            Announcement announcement = mAnnouncementMap.get(annId);
            announcement.getAttachment().setState(Attachment.STATE_OFFLINE);
            announcement.getAttachment().setFilePath(filepath);
            mDatabaseAccess.updateAttachmentFilePath(announcement.getId(), filepath);

            mAnnouncementMap.remove(annId);
            callDownloadFinished(announcement);
        }
    }

    public interface DownloadEventListener {
        void onDownloadStarted(Announcement announcement);
        void onDownloadProgressed(Announcement announcement, int progress);
        void onDownloadFailed(Announcement announcement);
        void onDownloadFinished(Announcement announcement);
    }
}
