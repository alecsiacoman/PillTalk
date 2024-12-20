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
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMedName;
    private TimePicker timePicker;
    private Button btnAddMed;
    private DatePicker datePicker;
    private TextView textView;
    private String voiceAlarm;

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

        editTextMedName = findViewById(R.id.editTextMedName);
        timePicker = findViewById(R.id.timePicker);
        btnAddMed = findViewById(R.id.buttonAddMed);
        datePicker = findViewById(R.id.datePicker);
        textView = findViewById(R.id.textView);

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

    public void speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            voiceAlarm = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            textView.setText(voiceAlarm);
            setMedicationByVoice(voiceAlarm);

        }
    }

    private void setMedicationByVoice(String str) {
        String timePattern = "(\\d{1,2})(:\\d{2})?\\s*(a\\.m\\.|am|p\\.m\\.|pm)";
        String dayPattern = "(today|monday|tuesday|wednesday|thursday|friday|saturday|sunday)";

        Log.d("Voice Message", str);

        String timeMatch = "";
        String dayMatch = "";

        java.util.regex.Matcher timeMatcher = java.util.regex.Pattern.compile(timePattern, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(str);
        if (timeMatcher.find())
            timeMatch = timeMatcher.group();

        java.util.regex.Matcher dayMatcher = java.util.regex.Pattern.compile(dayPattern, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(str);
        if (dayMatcher.find())
            dayMatch = dayMatcher.group();
        else
            dayMatch = "today"; // Default to today if no day is mentioned

        if (timeMatch.isEmpty()) {
            Toast.makeText(this, "Could not understand the time. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = Integer.parseInt(timeMatch.replaceAll("[^\\d]", ""));
        boolean isPM = timeMatch.toLowerCase().contains("pm");
        if (isPM && hour != 12) hour += 12;
        if (!isPM && hour == 12) hour = 0;

        int minute = 0;
        if (timeMatch.contains(":")) {
            minute = Integer.parseInt(timeMatch.split(":")[1].replaceAll("[^\\d]", ""));
        }

        Calendar now = Calendar.getInstance();
        Calendar notificationTime = Calendar.getInstance();

        if (!dayMatch.equalsIgnoreCase("today")) {
            int today = now.get(Calendar.DAY_OF_WEEK);
            int targetDay = getDayOfWeek(dayMatch.toLowerCase());
            int daysUntilTarget = (targetDay - today + 7) % 7;

            notificationTime.add(Calendar.DAY_OF_YEAR, daysUntilTarget);
        }

        notificationTime.set(Calendar.HOUR_OF_DAY, hour);
        notificationTime.set(Calendar.MINUTE, minute);
        notificationTime.set(Calendar.SECOND, 0);

        if (notificationTime.before(Calendar.getInstance())) {
            Toast.makeText(this, "The selected time is in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        scheduleNotification("Medication Reminder", notificationTime.getTimeInMillis());
        Toast.makeText(this, "Medication reminder set for " + dayMatch + " at " + timeMatch, Toast.LENGTH_SHORT).show();
    }

    private int getDayOfWeek(String day) {
        switch (day) {
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            case "sunday":
                return Calendar.SUNDAY;
            default:
                return -1;
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

        return new Medication(medName, year, month, day, hour, minute);
    }

}


