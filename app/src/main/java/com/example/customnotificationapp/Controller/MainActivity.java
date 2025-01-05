package com.example.customnotificationapp.Controller;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.customnotificationapp.Model.Medication;
import com.example.customnotificationapp.R;
import com.example.customnotificationapp.Repository.MedicationManager;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMedName;
    private TimePicker timePicker;
    private DatePicker datePicker;

    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    showToast("Post notification permission granted");
                } else {
                    showToast("Post notification permission not granted");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        Button btnAddMed = findViewById(R.id.buttonAddMed);
        btnAddMed.setOnClickListener(view -> handleAddMedication());
    }

    private void initializeViews() {
        editTextMedName = findViewById(R.id.editTextMedName);
        timePicker = findViewById(R.id.timePicker);
        datePicker = findViewById(R.id.datePicker);
    }

    private void handleAddMedication() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
        } else {
            Medication med = createMedicationFromInputs();
            setNotification(med);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        activityResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()){
                openExactAlarmSettings();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void openExactAlarmSettings() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivity(intent);
    }

    private boolean isNotificationTimeValid(Medication med) {
        // Create a Calendar instance to represent the time the medication is scheduled for
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(med.getYear(), med.getMonth(), med.getDay(), med.getHour(), med.getMinute(), 0);

        // Check if the notification time is before the current time
        if (notificationTime.before(Calendar.getInstance())) {
            showToast("The selected time is in the past!");
            return false;
        }
        return true;
    }


    private Medication createMedicationFromInputs() {
        return new Medication(
                editTextMedName.getText().toString(),
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                timePicker.getHour(),
                timePicker.getMinute(),
                (int) System.currentTimeMillis()
        );
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String voiceAlarm = Objects.requireNonNull(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)).get(0);
            setMedicationByVoice(voiceAlarm);
        }
    }

    private void setMedicationByVoice(String str) {
        String timePattern = "(\\d{1,2})(:\\d{2})?\\s*(a\\.m\\.|am|p\\.m\\.|pm)";
        String dayPattern = "(today|monday|tuesday|wednesday|thursday|friday|saturday|sunday)";

        String timeMatch = extractTime(str, timePattern);
        String dayMatch = extractDay(str, dayPattern);

        if (timeMatch.isEmpty() || dayMatch.isEmpty()) {
            showToast("Please specify time (am/pm) and date");
            return;
        }

        String medName = extractMedNameFromVoice(str);
        Medication med = createMedicationFromVoice(medName, timeMatch, dayMatch);

        setNotification(med);
    }



    private void setNotification(Medication med){
        if(med != null){
            if (isNotificationTimeValid(med)) {
                scheduleNotification(med, getNotificationTimeInMillis(med));
                MedicationManager.getInstance().addMedication(med);
            }
        }
    }

    private String extractTime(String str, String timePattern) {
        java.util.regex.Matcher timeMatcher = java.util.regex.Pattern.compile(timePattern, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(str);
        if (timeMatcher.find()) {
            return timeMatcher.group();
        }
        return "";
    }

    private String extractDay(String str, String dayPattern) {
        java.util.regex.Matcher dayMatcher = java.util.regex.Pattern.compile(dayPattern, java.util.regex.Pattern.CASE_INSENSITIVE).matcher(str);
        if (dayMatcher.find()) {
            return dayMatcher.group();
        }
        return "today";
    }

    private String extractMedNameFromVoice(String str) {
        String medName = "Medication";
        if (str.toLowerCase().contains("for")) {
            String[] words = str.split("\\s+");
            boolean foundFor = false;
            for (String word : words) {
                if (foundFor) {
                    medName = word;
                    break;
                }
                if (word.equalsIgnoreCase("for")) {
                    foundFor = true;
                }
            }
        }
        return medName;
    }

    private Medication createMedicationFromVoice(String medName, String timeMatch, String dayMatch) {
        int hour;
        int minute = 0;
        boolean isPM = timeMatch.toLowerCase().contains("p.m.");

        String[] timeParts = timeMatch.split(":");
        hour = Integer.parseInt(timeParts[0].trim());

        if (timeParts.length > 1) {
            minute = Integer.parseInt(timeParts[1].trim().replaceAll("\\D", ""));
        }

        // Fix time handling for AM/PM
        if (!isPM && hour == 12) hour = 0;

        int dayOfWeek = getDayOfWeekFromString(dayMatch);
        if (dayOfWeek == -1) {
            showToast("Please specify a valid day of the week (e.g., Monday, Tuesday, or Today).");
            return null;
        }

        // Fix for 'today' calculation
        Calendar calendar = Calendar.getInstance();
        if (dayMatch.equalsIgnoreCase("today")) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        } else {
            int todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int daysToAdd = (dayOfWeek - todayDayOfWeek + 7) % 7;
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        }

        return new Medication(medName, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute, (int) System.currentTimeMillis());
    }

    private int getDayOfWeekFromString(String dayMatch) {
        Log.d("Day of daymatch", dayMatch.toLowerCase());
        switch (dayMatch.toLowerCase()) {
            case "sunday":
                return Calendar.SUNDAY;
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
            case "today":
                return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            default:
                return -1;
        }
    }

    @SuppressLint("NewApi")
    private void scheduleNotification(Medication med, long triggerTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("title", "Meds Reminder");
            intent.putExtra("text", "It's time to take " + med.getMedName() + "!");
            intent.putExtra("id", med.getId());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            showToast("Medication reminder set!");
        } else {
            showToast("Please allow exact alarm permission in settings");
        }
    }

    private long getNotificationTimeInMillis(Medication med) {
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(med.getYear(), med.getMonth(), med.getDay(), med.getHour(), med.getMinute(), 0);
        return notificationTime.getTimeInMillis();
    }

    public void navigateToHomePage(View view) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
