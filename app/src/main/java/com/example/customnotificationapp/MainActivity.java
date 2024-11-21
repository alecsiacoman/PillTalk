package com.example.customnotificationapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
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
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMedName;
    private TimePicker timePicker;
    private Button btnAddMed;
    private Button btnVoice;
    private DatePicker datePicker;
    private VoiceRecognitionHelper voiceRecognitionHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceRecognitionHelper = new VoiceRecognitionHelper(this, this);

        btnVoice = findViewById(R.id.buttonVoice);
        editTextMedName = findViewById(R.id.editTextMedName);
        timePicker = findViewById(R.id.timePicker);
        btnAddMed = findViewById(R.id.buttonAddMed);
        datePicker = findViewById(R.id.datePicker);

        //creating a notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationHelper notificationHelper = new NotificationHelper(MainActivity.this, notificationManager);

        btnVoice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Check if the app has microphone permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
                } else {
                    // Permission already granted, start speech recognition
                    voiceRecognitionHelper.startListening();
                }
            }
        });
        btnAddMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                    }
                } else {
                    Medication med = setMedicamentation();

                    Calendar notificationTime = Calendar.getInstance();
                    notificationTime.set(med.getYear(), med.getMonth(), med.getDay(), med.getHour(), med.getMinute(), 0);

                    if (notificationTime.before(Calendar.getInstance())) {
                        Toast.makeText(MainActivity.this, "The selected time is in the past!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    scheduleNotification(med.getMedName(), notificationTime.getTimeInMillis());
                    Toast.makeText(MainActivity.this, "Notification set!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void handleVoiceCommand(String command){
        if(command.toLowerCase().contains("set reminder")){
            scheduleNotification("Notification set by voice", Calendar.getInstance().getTimeInMillis());
        }
    }

    @SuppressLint("NewApi")
    private void scheduleNotification(String medName, long triggerTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("title", "Meds Reminder");
            intent.putExtra("text", "It's time to take the " + medName + "!");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            Toast.makeText(this, "Medication reminder set!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please allow exact alarm permission in settings", Toast.LENGTH_SHORT).show();
        }
    }

    private Medication setMedicamentation(){
        //getting each element
        String medName = editTextMedName.getText().toString();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Log.d("My app", "medName" + year + " " + month + " " + day + " " + hour);
        return new Medication(medName, year, month, day, hour, minute);
    }
}


