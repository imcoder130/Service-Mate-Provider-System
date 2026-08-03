package com.example.servicemateproviderapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class provider_reminderpage extends AppCompatActivity implements NavigationBarView.OnItemReselectedListener {

    private EditText dateEditText, timeEditText;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button startCountdownButton,btnStop;
    private TextView countdownTextView;
    BottomNavigationView bnv;
    private CountDownTimer countDownTimer;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_reminderpage);

        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        startCountdownButton = findViewById(R.id.startCountdownButton);
        countdownTextView = findViewById(R.id.countdownTextView);
        btnStop = findViewById(R.id.btnStop);

        bnv = findViewById(R.id.bottomnavigation);
        bnv.setOnItemReselectedListener(this);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        startCountdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCountdown();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(provider_reminderpage.this, com.example.servicemateproviderapp.MusicService.class));
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        dateEditText.setText(dateFormat.format(selectedDate.getTime()));
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        timeEditText.setText(timeFormat.format(selectedTime.getTime()));
                    }
                }, hour, minute, false);

        timePickerDialog.show();
    }

    private void startCountdown() {
        String dateString = dateEditText.getText().toString();
        String timeString = timeEditText.getText().toString();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
        try {
            Calendar targetDateTime = Calendar.getInstance();
            targetDateTime.setTime(dateTimeFormat.parse(dateString + " " + timeString));

            //For increament time
            // targetDateTime.add(Calendar.HOUR_OF_DAY,-1);

            long targetTimeInMillis = targetDateTime.getTimeInMillis();
            long currentTimeInMillis = System.currentTimeMillis();
            long timeDiffInMillis = targetTimeInMillis - currentTimeInMillis;

            if (timeDiffInMillis <= 0) {
                countdownTextView.setText("Invalid date/time");
                return;
            }

            countDownTimer = new CountDownTimer(timeDiffInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long hours = seconds / 3600;
                    seconds %= 3600;
                    long minutes = seconds / 60;
                    seconds %= 60;

                    String countdownText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    countdownTextView.setText(countdownText);
                }

                @Override
                public void onFinish() {
                    countdownTextView.setText("Countdown Finished");
                    startService(new Intent(provider_reminderpage.this, com.example.servicemateproviderapp.MusicService.class));

                }
            };

            countDownTimer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Account) {
            Intent i = new Intent(getApplicationContext(), ProviderAccount.class);
            startActivity(i);
        } else if (id == R.id.Notification) {
            Intent i = new Intent(getApplicationContext(), ProviderNotification.class);
            startActivity(i);

        } else if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), ProviderHome.class);
            startActivity(i);

        } else if (id == R.id.Reminder) {
            Intent i = new Intent(getApplicationContext(), provider_reminderpage.class);
            startActivity(i);

        } else if (id == R.id.onoff) {
            Intent i = new Intent(getApplicationContext(),OnOffPage.class);
            startActivity(i);
        }
    }
}

