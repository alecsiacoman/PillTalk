package com.example.customnotificationapp;

import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean granted) {
                    if (granted) {
                        Toast.makeText(MainActivity.this, "Post notification permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Post notification permission not granted", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private EditText editTextMedName;
    private TimePicker timePicker;
    private Button btnAddMed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMedName = findViewById(R.id.editTextMedName);
        timePicker = findViewById(R.id.timePicker);
        btnAddMed = findViewById(R.id.buttonAddMed);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationHelper notificationHelper = new NotificationHelper(MainActivity.this, notificationManager);

        btnAddMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    String medName = editTextMedName.getText().toString();
                    int hour = timePicker.getHour();
                    int minute = timePicker.getMinute();

                    Calendar notificationTime = Calendar.getInstance();
                    notificationTime.set(Calendar.HOUR_OF_DAY, hour);
                    notificationTime.set(Calendar.MINUTE, minute);
                    notificationTime.set(Calendar.SECOND, 0);
                    notificationTime.set(Calendar.MILLISECOND , 0);

                    //Schedule the notification
                    notificationHelper.createNotification("Meds Reminder", "It's time to take the " + medName + "!");
                }
            }
        });
    }
}
