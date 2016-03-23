package com.jumpintorivet.rivet.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.LaunchSplashActivity;
import com.jumpintorivet.rivet.activities.bar_on_bottom.BarOnBottomActivity;
import com.jumpintorivet.rivet.activities.conversation_making.ConversationMakingActivity;
import com.jumpintorivet.rivet.application.Foreground;
import com.jumpintorivet.rivet.application.MyApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class RivetGCMListenerService extends GcmListenerService {
    @Inject
    Bus bus;

    public static void giveRivetNotification(Context context, int notificationId, String title, String body, String appSection) {
        PendingIntent pendingIntent;
        if (appSection == null || appSection.equals("conversation")) {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                    .addNextIntent(new Intent(context, BarOnBottomActivity.class))
                    .addNextIntent(new Intent(context, ConversationMakingActivity.class));
            pendingIntent = stackBuilder.getPendingIntent((int) (System.currentTimeMillis() & 0xfffffff), PendingIntent.FLAG_CANCEL_CURRENT);
        } else if (appSection.equals("conversation_list")) {
            pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), new Intent(context, LaunchSplashActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(context, (int) (System.currentTimeMillis() & 0xfffffff), null, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setTicker(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{200, 200, 400})
                .setLights(Color.GREEN, 500, 500)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApplication) getApplication()).inject(this);
    }

    @Override
    public void onMessageReceived(String from, final Bundle data) {
        if (Foreground.get().isBackground()) {
            giveRivetNotification(getApplicationContext(), data.size(), data.getString("title"), data.getString("body"), data.getString("app_section"));
        } else if (data.getString("app_section") != null && data.getString("app_section").equals("conversation")) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    bus.post(new NotificationReceivedEvent(data.getString("body")));
                }
            });
        }
    }

    public static class NotificationReceivedEvent {
        private String body;

        public NotificationReceivedEvent(String body) {
            this.body = body;
        }

        public String getBody() {
            return body;
        }
    }
}
