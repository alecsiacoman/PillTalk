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
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0); // Default ID is 0 if not found

        if("SNOOZE_ACTION".equals(intent.getAction())){
            // Reschedule the notification to appear in 5 minutes
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent snoozeIntent = new Intent(context, NotificationReceiver.class);
            snoozeIntent.setAction("SHOW_NOTIFICATION");
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long snoozeTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5 minutes later
            alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, snoozePendingIntent);

        } else if("DONE_ACTION".equals(intent.getAction())) {
            // Cancel the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId); // Use the same ID as when showing the notification

        }
    }
}
