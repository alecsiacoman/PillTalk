package com.example.customnotificationapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        NotificationHelper notificationHelper = new NotificationHelper(context, context.getSystemService(NotificationManager.class));
        notificationHelper.createNotification(title, text);
    }
}
