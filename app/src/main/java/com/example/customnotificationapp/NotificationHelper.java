package com.example.customnotificationapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private final Context context;
    private final NotificationManager notificationManager;

    private static final String CHANNEL_ID = "medication_notification_channel"; // Updated to be more unique

    public NotificationHelper(Context context, NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Meds", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This channel is for sending notifications about meds.");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void createNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
