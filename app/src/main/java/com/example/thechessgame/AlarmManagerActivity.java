package com.example.thechessgame;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class AlarmManagerActivity extends AppCompatActivity {

    private TextView textViewDateTime;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);

        textViewDateTime = findViewById(R.id.textViewDateTime);
        Button buttonChooseDateTime = findViewById(R.id.buttonChooseDateTime);
        Button buttonSetAlarm = findViewById(R.id.buttonSetAlarm);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        buttonChooseDateTime.setOnClickListener(v -> chooseDateTime());
        buttonSetAlarm.setOnClickListener(v -> setAlarm());

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
        findViewById(R.id.buttonBackToOpening).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmManagerActivity.this, OpeningActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void chooseDateTime() {
        calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                updateDateTimeText();
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void updateDateTimeText() {
        textViewDateTime.setText(String.format("Selected date and time: %02d-%02d-%04d %02d:%02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)));
    }

    private void setAlarm() {
        if (calendar == null) {
            Toast.makeText(this, "Please choose a date and time first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm set for " + textViewDateTime.getText(), Toast.LENGTH_SHORT).show();
            String alarmSetMessage = "Alarm set for " + textViewDateTime.getText();
            textToSpeech.speak(alarmSetMessage, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
