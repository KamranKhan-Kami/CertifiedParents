package com.ellalan.certifiedparent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;


public class MyBootReceiver extends BroadcastReceiver {
    private PendingIntent pendingIntent;
    private AlarmManager manager;


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmIntent = new Intent(context, CPBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long time = context.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, Context.MODE_PRIVATE)
                .getLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, 0);

        if (time != 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                }
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            }

        }


    }
}
