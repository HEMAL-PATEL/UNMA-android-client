package com.paperplanes.unma.infrastructure;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.announcementdetail.AnnouncementDetailActivity;
import com.paperplanes.unma.common.AppUtil;
import com.paperplanes.unma.data.AnnouncementRepository;

import java.net.URL;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.observers.DisposableCompletableObserver;

/**
 * Created by abdularis on 16/11/17.
 *
 * This class will receive a firebase message sent by backend server
 * we don't send our notification using data field in the firebase request
 * so onMessageReceived() method always get called, and we do things manually
 * notifikasi kita buat secara manual sesuai kondisi is app in the foreground or not
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    private static final String TAG = FirebaseNotificationService.class.getSimpleName();

    private static final String REMOTE_DATA_KEY_ANNOUNCEMENT_ID = "id";
    private static final String REMOTE_DATA_KEY_ANNOUNCEMENT_TITLE = "title";

    @Inject
    AnnouncementRepository mAnnouncementRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();

        mAnnouncementRepository.fetchAnnouncements()
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Data fetched");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d(TAG, "Failed to fetch data: " + throwable.toString());
                    }
                });

        String packageName = this.getPackageName();
        if (!AppUtil.isAppInForeground(this, packageName)) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Log.d(TAG, "Showing notification");

                Notification notification = buildNotification(data);
                notificationManager.notify(data.get(REMOTE_DATA_KEY_ANNOUNCEMENT_ID), 0, notification);
            }
        }
        else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, getInAppNotificationSoundUri());
            ringtone.play();
        }
    }

    private Notification buildNotification(Map<String, String> data) {
        String announcementId = data.get(REMOTE_DATA_KEY_ANNOUNCEMENT_ID);
        String announcementTitle = data.get(REMOTE_DATA_KEY_ANNOUNCEMENT_TITLE);
        String notifTitle = getString(R.string.notification_title);
        String notifTicker = notifTitle + ": " + announcementTitle;
        if (announcementTitle.length() > 96)
            notifTicker = notifTitle + ": " + announcementTitle.substring(0, 96) + "...";

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(notifTitle);
        bigTextStyle.bigText(announcementTitle);

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(this)
                        .setContentIntent(createNotificationPendingIntent(announcementId))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(notifTitle)
                        .setContentText(announcementTitle)
                        .setTicker(notifTicker)
                        .setAutoCancel(true)
                        .setSound(getNotificationSoundUri())
                        .setStyle(bigTextStyle);

        return notifBuilder.build();
    }

    private PendingIntent createNotificationPendingIntent(String announcementId) {
        Intent openAnnouncementDetailIntent = new Intent(this, AnnouncementDetailActivity.class);
        openAnnouncementDetailIntent.putExtra(AnnouncementDetailActivity.EXTRA_ANNOUNCEMENT_ID, announcementId);
        return PendingIntent.getActivity(
                this,
                0,
                openAnnouncementDetailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Uri getNotificationSoundUri() {
        return getSoundUriFromPreferences(getString(R.string.pref_notification_sound));
    }

    private Uri getInAppNotificationSoundUri() {
        return getSoundUriFromPreferences(getString(R.string.pref_in_app_notification_sound));
    }

    private Uri getSoundUriFromPreferences(String prefName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String soundUri = prefs.getString(prefName, null);
        Log.d(TAG, soundUri);
        if (soundUri != null)
            return Uri.parse(soundUri);
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }
}
