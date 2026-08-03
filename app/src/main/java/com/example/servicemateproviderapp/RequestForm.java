package com.example.servicemateproviderapp;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText time, date;
    int hour, minute;
    int day, year, month;
    Button btnRequest;
    Spinner spin;
    String spce1[] = {"Shivajinagar","Deccan Gymkhana","JM Road","FC Road","Pune Camp","Sadashiv Peth","Narayan Peth","Budhwar Peth","Kothrud","Bavdhan","Warje","Karve Nagar", "Hingne Khurd", "Sinhagad Road"};

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    private String name;
    private String id;
    private String UserId;

    String CurrentuserId;
    String CurrentUsername;

    ArrayList<MyAdapter.ServiceStop> serviceStops; // Store service stop data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_form);
        date = findViewById(R.id.dateformate);
        time = findViewById(R.id.timeformate);
        spin = findViewById(R.id.spinner000);
        btnRequest = findViewById(R.id.reuestbtn);

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //code for Spinner for get address for user
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spce1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrayAdapter);

        UserId = firebaseAuth.getCurrentUser().getUid();

        //get provider id from MyAdapter class
        Intent i = getIntent();
        name = i.getStringExtra("name");
        id = i.getStringExtra("PPId");

        // Fetch service stop data
        serviceStops = new ArrayList<>();
        fetchServiceStopData(id); // Fetch service stop data for the selected provider

        // For select time
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(RequestForm.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                                hour = selectedHour;
                                minute = selectedMinute;

                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);

                                // Set office hours (9 am to 6 pm)
                                Calendar officeStartTime = Calendar.getInstance();
                                officeStartTime.set(Calendar.HOUR_OF_DAY, 9);
                                officeStartTime.set(Calendar.MINUTE, 0);

                                Calendar officeEndTime = Calendar.getInstance();
                                officeEndTime.set(Calendar.HOUR_OF_DAY, 18);
                                officeEndTime.set(Calendar.MINUTE, 0);

                                // Check if the selected time is within office hours
                                if (calendar.after(officeStartTime) && calendar.before(officeEndTime)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                    time.setText(sdf.format(calendar.getTime()));
                                } else {
                                    Toast.makeText(RequestForm.this, "Please select a time within office hours (9 am to 6 pm).", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, hour, minute, false); // Set is24HourView to false to show AM/PM picker
                timePickerDialog.show();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DatePickerDialog code with disabled dates
                showDatePickerDialog();
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send request button code
                showMyDialog();
            }
        });
    }

    private void fetchServiceStopData(String providerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ServiceStop")
                .whereEqualTo("ProviderId", providerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "Service stop data fetched successfully.");
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String startDate = documentSnapshot.getString("StartDate");
                        String endDate = documentSnapshot.getString("EndDate");
                        serviceStops.add(new MyAdapter.ServiceStop(providerId, startDate, endDate));
                    }
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error fetching service stop data: " + e.getMessage()));
    }

    private void showDatePickerDialog() {
        // Date picker dialog code
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 2);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RequestForm.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDayOfMonth;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        // Check if the selected date is within the next 2 months
                        if (calendar.after(minDate) || calendar.before(maxDate) || calendar.equals(minDate)) {
                            // Check if the selected date is not within service stop period
                            if (!isDateInServiceStopPeriod(calendar.getTime())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                date.setText(sdf.format(calendar.getTime()));
                            } else {
                                Toast.makeText(RequestForm.this, "This date provider can not be available. Please select another date.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RequestForm.this, "Please select a date within the next 2 months.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, year, month, day);

        // Set min date to current date
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Set max date to 2 months from current date
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean isDateInServiceStopPeriod(Date selectedDate) {
        for (MyAdapter.ServiceStop serviceStop : serviceStops) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date startDate = sdf.parse(serviceStop.getStartDate());
                Date endDate = sdf.parse(serviceStop.getEndDate());
                // Check if the selected date is between start date and end date of service stop
                if (selectedDate.after(startDate) && selectedDate.before(endDate)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the provider");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth = FirebaseAuth.getInstance();
                firestore = FirebaseFirestore.getInstance();

                CurrentuserId = firebaseAuth.getCurrentUser().getUid(); // Initialize userId

                DocumentReference documentReference = firestore.collection("users").document(CurrentuserId);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null && value.exists()) {
                            CurrentUsername = value.getString("FName"); // Set the value of name here
                            // You can update UI elements here if needed
                        }
                    }
                });

                String s1 = spin.getSelectedItem().toString();
                String s2 = date.getText().toString();    // assuming dateEditText is your EditText for the date
                String s3 = time.getText().toString();    // assuming timeEditText is your EditText for the time

                //Store data in Database
                DocumentReference documentReference2 = firestore.collection("Service").document(id);
                Map<String, Object> user = new HashMap<>();
                user.put("UserId", UserId);
                user.put("ProviderId", id);
                user.put("ServiceDate", s2);
                user.put("ServiceTime", s3);
                user.put("UserAddress", s1);

                documentReference2.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "On Success: User profile is created " + id);
                        Log.d("Tag", "data stored");
                        Toast.makeText(RequestForm.this, "data saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "On Failure: " + e.toString());
                    }
                });

                // Save the data to SharedPreferences
                //and share msg to provider Notification File
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Assuming you stored the username during login
                editor.putString("value0", name);
                editor.putString("value1", s1); //address
                editor.putString("value2", s2);  // Pass the date
                editor.putString("value3", s3);  // Pass the time
                editor.putString("ppId", id);
                editor.putString("UserName", CurrentUsername);

                //share User Id to Provider Notification
                editor.putString("UserId", UserId);
                editor.apply();
                Toast.makeText(RequestForm.this, "UserName" + CurrentUsername, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when Cancel button is clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Spinner item selected code
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing selected code
    }
}
