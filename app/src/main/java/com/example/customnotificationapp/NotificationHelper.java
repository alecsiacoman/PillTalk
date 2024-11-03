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

    private final int NOTIFICATION_ID;

    public NotificationHelper(Context context, NotificationManager notificationManager, int id) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.NOTIFICATION_ID = id;
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
//        // Intent for Snooze action
//        Intent snoozeIntent = new Intent(context, NotificationReceiver.class);
//        snoozeIntent.setAction("SNOOZE_ACTION");
//        snoozeIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
//        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Intent for Done action
//        Intent doneIntent = new Intent(context, NotificationReceiver.class);
//        doneIntent.setAction("DONE_ACTION");
//        doneIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
//        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context, 1, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .addAction(R.drawable.baseline_snooze_24, "Snooze", snoozePendingIntent)
//                .addAction(R.drawable.baseline_done_24, "Done", donePendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
