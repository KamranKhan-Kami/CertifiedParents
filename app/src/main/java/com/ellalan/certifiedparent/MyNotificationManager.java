package com.ellalan.certifiedparent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

public class MyNotificationManager {
    private Context context;
    public static final int NOTIFICATION_ID = 101;
    NotificationManager notificationManager;

    public MyNotificationManager(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(String title, String notification, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.parenting_challenge_notification);
        builder.setContentTitle(title);
        builder.setSound(alarmSound);
        builder.setColor(context.getResources().getColor(R.color.green_500));
        builder.setContentText(notification);
        builder.setContentIntent(pendingIntent);
        Notification mNotification = builder.build();

        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public void dismissNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
