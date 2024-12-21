package com.example.customnotificationapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        NotificationHelper notificationHelper = new NotificationHelper(context, context.getSystemService(NotificationManager.class));
        notificationHelper.createNotification(title, text);

        int medId = intent.getIntExtra("id", -1);

        HomeActivity homeActivity = HomeActivity.getInstance();
        if (homeActivity != null) {
            List<Medication> medicationList = homeActivity.getMedicationList();
            Medication toRemove = null;
            for (Medication med : medicationList) {
                if (med.getId() == medId) {
                    toRemove = med;
                    break;
                }
            }

            if (toRemove != null) {
                medicationList.remove(toRemove);
                homeActivity.getMedicationAdapter().notifyDataSetChanged();
            }
        }
    }
}
