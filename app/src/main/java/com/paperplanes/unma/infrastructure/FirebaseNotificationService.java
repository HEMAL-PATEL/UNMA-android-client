package com.paperplanes.unma.infrastructure;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paperplanes.unma.App;
import com.paperplanes.unma.R;
import com.paperplanes.unma.announcementdetail.AnnouncementDetailActivity;
import com.paperplanes.unma.common.AppUtil;
import com.paperplanes.unma.data.AnnouncementRepository;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.observers.DisposableCompletableObserver;

/**
 * Created by abdularis on 16/11/17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    private static final String TAG = FirebaseNotificationService.class.getSimpleName();

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

        Uri notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this, notif);
        ringtone.play();

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

            Intent openAnnouncementDetailIntent = new Intent(this, AnnouncementDetailActivity.class);
            openAnnouncementDetailIntent.putExtra(AnnouncementDetailActivity.EXTRA_ANNOUNCEMENT_ID, data.get("id"));
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0,
                            openAnnouncementDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notifBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Pemberitahuan")
                            .setContentText(data.get("title"))
                            .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Log.d(TAG, "Showing notification");
                notificationManager.notify(0, notifBuilder.build());
            }
        }
    }
}
