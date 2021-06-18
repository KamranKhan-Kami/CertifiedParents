package com.ellalan.certifiedparent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.content.WakefulBroadcastReceiver;
public class CPBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        MyNotificationManager myNotificationManager = new MyNotificationManager(context);
        Intent i = new Intent(context, SplashActivity.class);
        myNotificationManager.showNotification("New question", "Dear parent, a new question is now available", i);

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, false).apply();
        sharedPreferences.edit().putLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, 0).apply();

    }
}
