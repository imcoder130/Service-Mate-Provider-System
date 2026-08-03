package com.example.servicemateproviderapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OnOffPage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView txt1, txt2;
    private EditText StartDate, EndDate;

    Button StopService;

    private BottomNavigationView bnv;

    int hour, minute;
    int day, year, month;

    String serviceDate;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    private String ProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_off_page);
        txt1 = findViewById(R.id.textview2);
        txt2 = findViewById(R.id.textview3);
        StartDate = findViewById(R.id.startdate);
        EndDate = findViewById(R.id.enddate);

        StopService = findViewById(R.id.stopservice);

        bnv = findViewById(R.id.bottomnavigation);
        bnv.setOnNavigationItemSelectedListener(this);

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        ProviderId = firebaseAuth.getCurrentUser().getUid();

        StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.MONTH, 2);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OnOffPage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                year = selectedYear;
                                month = selectedMonth;
                                day = selectedDayOfMonth;

                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                // Check if the selected date is within the next 2 months
                                if (calendar.after(minDate) || calendar.before(maxDate) || calendar.equals(minDate)) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    StartDate.setText(sdf.format(calendar.getTime()));
                                }

                                else {
                                    Toast.makeText(OnOffPage.this, "Please select a date within the next 2 months.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, year, month, day);

                // Set min date to current date
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

                // Set max date to 2 months from current date
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });




        EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.MONTH, 2);

                DatePickerDialog datePickerDialog = new DatePickerDialog(OnOffPage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                year = selectedYear;
                                month = selectedMonth;
                                day = selectedDayOfMonth;

                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                // Check if the selected date is within the next 2 months
                                if (calendar.after(minDate) || calendar.before(maxDate) || calendar.equals(minDate)) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    EndDate.setText(sdf.format(calendar.getTime()));
                                }

                                else {
                                    Toast.makeText(OnOffPage.this, "Please select a date within the next 2 months.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, year, month, day);

                // Set min date to current date
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

                // Set max date to 2 months from current date
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        StopService.setOnClickListener(v -> {
            String SDate = StartDate.getText().toString();
            String EDate = EndDate.getText().toString();

            // Check if dates are selected
            if (SDate.isEmpty() || EDate.isEmpty()) {
                Toast.makeText(OnOffPage.this, "Please select start and end dates", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the provider has previous bookings during the selected holidays
            stopService(SDate, EDate);
        });

    }

    private void showDatePickerDialog(final EditText editText) {
        // Date picker dialog code
    }

    private void stopService(String startDate, String endDate) {
        ProviderId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("ServiceStop").document(ProviderId);
        Map<String, Object> user = new HashMap<>();
        user.put("ProviderId", ProviderId);
        user.put("StartDate", startDate);
        user.put("EndDate", endDate);

        ProviderId = firebaseAuth.getCurrentUser().getUid(); // You forgot to initialize UserId

        DocumentReference documentReference2 = firestore.collection("Service").document(ProviderId);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    serviceDate = value.getString("ServiceDate");
                    // Check if the service date is between the start date and end date
                    if (serviceDate != null && !serviceDate.isEmpty()) {
                        if (isServiceDateBetween(serviceDate, startDate, endDate)) {
                            Toast.makeText(OnOffPage.this, "Service date falls within the selected period. Cannot stop service.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Proceed with stopping the service
                            checkPreviousBookings(startDate, endDate, user, documentReference);
                        }
                    } else {
                        Toast.makeText(OnOffPage.this, "Service date not found or invalid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    checkPreviousBookings(startDate, endDate, user, documentReference);

                }
            }
        });
    }

    // Function to check if service date is between start date and end date
    private boolean isServiceDateBetween(String serviceDate, String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date serviceDateTime = sdf.parse(serviceDate);
            Date startDateTime = sdf.parse(startDate);
            Date endDateTime = sdf.parse(endDate);

            return serviceDateTime.after(startDateTime) && serviceDateTime.before(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void checkPreviousBookings(String startDate, String endDate, Map<String, Object> user, DocumentReference documentReference) {
        String providerId = firebaseAuth.getCurrentUser().getUid();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date startDateTime, endDateTime;
        try {
            startDateTime = sdf.parse(startDate);
            endDateTime = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("Service")
                .whereEqualTo("ProviderId", providerId)
                .whereGreaterThanOrEqualTo("ServiceDate", startDateTime)
                .whereLessThanOrEqualTo("ServiceDate", endDateTime)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Provider has bookings during the selected holidays
                        Toast.makeText(OnOffPage.this, "You have previous bookings during the selected holidays. Unable to stop service.", Toast.LENGTH_SHORT).show();
                    } else {
                        // No bookings during holidays, proceed to stop service
                        documentReference.set(user)
                                .addOnSuccessListener(unused -> {
                                    Log.d("TAG", "Data Save " + ProviderId);
                                    Toast.makeText(OnOffPage.this, "Your Service is Stopped", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("TAG", "On Failure: " + e.toString());
                                    Toast.makeText(OnOffPage.this, "Failed to stop service", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Error checking bookings: " + e.getMessage());
                    Toast.makeText(OnOffPage.this, "Failed to check bookings", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            Intent i = new Intent(getApplicationContext(), OnOffPage.class);
            startActivity(i);
        }
        return false;
    }

}
