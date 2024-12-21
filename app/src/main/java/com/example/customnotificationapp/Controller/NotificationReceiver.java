package com.example.customnotificationapp.Controller;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.customnotificationapp.Model.Medication;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @SuppressLint("NotifyDataSetChanged")
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
