package com.example.customnotificationapp;

import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMedName = findViewById(R.id.editTextMedName);
        timePicker = findViewById(R.id.timePicker);
        btnAddMed = findViewById(R.id.buttonAddMed);
        datePicker = findViewById(R.id.datePicker);

        //creating a notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationHelper notificationHelper = new NotificationHelper(MainActivity.this, notificationManager);

        btnAddMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    Medicamentation med = setMedicamentation();

                    Calendar notificationTime = Calendar.getInstance();
                    notificationTime.set(med.getYear(), med.getMonth(), med.getDay(), med.getHour(), med.getMinute(), 0);

                    //Schedule the notification
                    notificationHelper.createNotification("Meds Reminder", "It's time to take the " + med.getMedName() + "!");
                }
            }
        });
    }

    private Medicamentation setMedicamentation(){
        //getting each element
        String medName = editTextMedName.getText().toString();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        return new Medicamentation(medName, year, month, day, hour, minute);
    }
}


