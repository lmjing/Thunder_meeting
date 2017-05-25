package com.sopt.android.thunder.GCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sopt.android.thunder.R;
import com.sopt.android.thunder.v3.Splash;

/**
 * Created by Hong on 2016-01-12.
 */
public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /*
     * from SenderID 값을 받아온다.
     *data Set형태로 GCM으로 받은 데이터 payload이다.
     */

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("msg");


        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);

        sendNotification(title, message);
    }

    private void sendNotification(String title, String msg) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setSmallIcon(R.drawable.splash_icon);
            mBuilder.setTicker("여기여기붙어라에서 새로운 번개가 등록되었습니다");
            mBuilder.setWhen(System.currentTimeMillis());
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(msg);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.InboxStyle style = new Notification.InboxStyle(mBuilder);

            mBuilder.setStyle(style);

            nm.notify(555, mBuilder.build());
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.splash_icon)
                    .setTicker("여기여기붙어라에서 새로운 번개가 등록되었습니다")
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}
