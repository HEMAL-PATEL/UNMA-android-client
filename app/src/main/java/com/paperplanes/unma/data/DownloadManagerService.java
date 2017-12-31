package com.paperplanes.unma.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.paperplanes.unma.App;
import com.paperplanes.unma.common.FileUtil;
import com.paperplanes.unma.common.exceptions.ExternalStorageUnavailableException;
import com.paperplanes.unma.data.network.api.AnnouncementApi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by abdularis on 03/12/17.
 */

public class DownloadManagerService extends IntentService {
    private static final String TAG = DownloadManagerService.class.getSimpleName();

    private static final long PROGRESS_UPDATE_INTERVAL = 1000L;

    public static final String ACTION_DOWNLOAD_ATTACHMENT = "DOWNLOAD_FILE";

    public static final String ACTION_DOWNLOAD_PROGRESSED = "DOWNLOAD_PROGRESSED";
    public static final String ACTION_DOWNLOAD_STARTED = "DOWNLOAD_STARTED";
    public static final String ACTION_DOWNLOAD_FINISHED = "DOWNLOAD_FINISHED";
    public static final String ACTION_DOWNLOAD_FAILED = "DOWNLOAD_FAILED";
    public static final String ACTION_DOWNLOAD_CONNECTING = "DOWNLOAD_CONNECTING";

    public static final String EXTRA_DOWNLOAD_REQUEST = "DOWNLOAD_REQUEST";
    public static final String EXTRA_ANNOUNCEMENT_ID = "ANNOUNCEMENT_ID";
    public static final String EXTRA_DOWNLOAD_PROGRESS = "DOWNLOAD_PROGRESS";
    public static final String EXTRA_DOWNLOAD_FILE_PATH = "DOWNLOAD_FILE_PATH";

    private LocalBroadcastManager mBroadcastManager;
    private NotificationCompat.Builder mNotifBuilder;
    private NotificationManager mNotifManager;

    @Inject
    AnnouncementApi mAnnouncementService;

    public DownloadManagerService() {
        super("DownloadManagerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true);
        mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotifManager != null) {
            mNotifManager.cancel(0);
        }
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        ((App) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;

            if (action.equals(ACTION_DOWNLOAD_ATTACHMENT)) {
                AttachmentDownloadRequest request = intent.getParcelableExtra(EXTRA_DOWNLOAD_REQUEST);
                if (request != null) {
                    download(request);
                }
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        mNotifManager.cancel(0);
    }

    private void publishDownloadProgress(AttachmentDownloadRequest request, int progress) {
        mNotifBuilder
                .setProgress(100, progress, false)
                .setContentText(progress + "%");
        mNotifManager.notify(request.getAnnouncementId(), 0, mNotifBuilder.build());

        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_PROGRESSED);
        intent.putExtra(EXTRA_ANNOUNCEMENT_ID, request.getAnnouncementId());
        intent.putExtra(EXTRA_DOWNLOAD_PROGRESS, progress);
        mBroadcastManager.sendBroadcast(intent);
    }

    private void publishDownloadStarted(AttachmentDownloadRequest request) {
        mNotifBuilder
                .setProgress(100, 0, true)
                .setContentTitle(request.getAttachmentName());
        mNotifManager.notify(request.getAnnouncementId(), 0, mNotifBuilder.build());

        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_STARTED);
        intent.putExtra(EXTRA_ANNOUNCEMENT_ID, request.getAnnouncementId());
        mBroadcastManager.sendBroadcast(intent);
    }

    private void publishDownloadFailed(AttachmentDownloadRequest request) {
        mNotifManager.cancel(request.getAnnouncementId(), 0);

        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_FAILED);
        intent.putExtra(EXTRA_ANNOUNCEMENT_ID, request.getAnnouncementId());
        mBroadcastManager.sendBroadcast(intent);
    }

    private void publishDownloadFinish(AttachmentDownloadRequest request, String filepath) {
        mNotifManager.cancel(request.getAnnouncementId(), 0);

        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_FINISHED);
        intent.putExtra(EXTRA_ANNOUNCEMENT_ID, request.getAnnouncementId());
        intent.putExtra(EXTRA_DOWNLOAD_FILE_PATH, filepath);
        mBroadcastManager.sendBroadcast(intent);
    }

    private void publishDownloadConnecting(AttachmentDownloadRequest request) {
        Intent intent = new Intent();
        intent.setAction(ACTION_DOWNLOAD_CONNECTING);
        intent.putExtra(EXTRA_ANNOUNCEMENT_ID, request.getAnnouncementId());
        mBroadcastManager.sendBroadcast(intent);
    }

    private void download(AttachmentDownloadRequest request) {
        String announcementId = request.getAnnouncementId();
        publishDownloadConnecting(request);
        mAnnouncementService.downloadAttachment(request.getAttachmentUrl())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody resp) {
                        if (!FileUtil.isExternalStorageAvailable())
                            throw new ExternalStorageUnavailableException();

                        String folder = DownloadManager.ROOT_DOWNLOAD_DIR + File.separator + announcementId;
                        File dir = new File(folder);
                        if (!dir.exists() && !dir.mkdirs()) {
                            publishDownloadFailed(request);
                            return;
                        }
                        File file = new File(dir, request.getAttachmentName() + ".temp");

                        InputStream inputStream;
                        OutputStream outputStream;
                        try {
                            byte[] buffer = new byte[4096];

                            inputStream = new BufferedInputStream(resp.byteStream(), 1024 * 8);
                            outputStream = new FileOutputStream(file);

                            long fileSize = resp.contentLength();
                            long downloaded = 0;

                            long startTime = System.currentTimeMillis();
                            long elapsedTime;
                            int read;

                            publishDownloadStarted(request);
                            while ((read = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, read);
                                downloaded += read;

                                elapsedTime = System.currentTimeMillis() - startTime;
                                if (elapsedTime >= PROGRESS_UPDATE_INTERVAL) {
                                    startTime = System.currentTimeMillis();
                                    publishDownloadProgress(request, (int) (downloaded * 100 / fileSize));
                                }
                            }

                            outputStream.flush();
                            outputStream.close();
                            inputStream.close();
                            File destFile = new File(folder, request.getAttachmentName());
                            if (!file.renameTo(destFile)) {
                                if (file.delete())
                                    Log.d(TAG, "Delete : " + file.getAbsolutePath());
                                if (dir.delete())
                                    Log.d(TAG, "Delete Dir : " + dir.getAbsolutePath());
                                return;
                            }
                            publishDownloadFinish(request, destFile.getAbsolutePath());

                        } catch (Exception e) {
                            publishDownloadFailed(request);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        publishDownloadFailed(request);
                    }
                });
    }
}
