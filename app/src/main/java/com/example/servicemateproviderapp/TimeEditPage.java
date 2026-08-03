package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimeEditPage extends AppCompatActivity {

    EditText Time, Date;
    Button okbtn;
    int hour, minute;
    int day, year, month;
    public static String timeeditmsg = "";

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    String ProviderId;

    String currentProName;
    private String date;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_edit_page);
        Time = findViewById(R.id.edittime);
        Date = findViewById(R.id.editdate);
        okbtn = findViewById(R.id.okbutton);


        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ProviderId = firebaseAuth.getCurrentUser().getUid(); // Initialize userId

        DocumentReference documentReference = firestore.collection("Providers").document(ProviderId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    currentProName = value.getString("FName"); // Set the value of name here
                    // You can update UI elements here if needed
                }
            }
        });

        Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(TimeEditPage.this,
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
                                   Time.setText(sdf.format(calendar.getTime()));
                                } else {
                                    Toast.makeText(TimeEditPage.this, "Please select a time within office hours (9 am to 6 pm).", Toast.LENGTH_SHORT).show();
                                    // You can optionally reset the time field or take other actions here
                                }
                            }
                        }, hour, minute, false); // Set is24HourView to false to show AM/PM picker
                timePickerDialog.show();
            }
        });

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.MONTH, 2);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TimeEditPage.this,
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
                                    Date.setText(sdf.format(calendar.getTime()));
                                }

                                else {
                                    Toast.makeText(TimeEditPage.this, "Please select a date within the next 2 months.", Toast.LENGTH_SHORT).show();
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

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showMyDialog();




            }
        });


    }

    private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the user");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                date = Date.getText().toString();
                time = Time.getText().toString();

                SharedPreferences sharedPreferencess = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String USERID = sharedPreferencess.getString("USERID", "");

                Log.d("TAG",USERID);

                DocumentReference documentReference = firestore.collection("Service").document(ProviderId );
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve OrderCount and sumRate from Firestore
//                            String orderCountString = documentSnapshot.getString("POrder");
//                            String sumRateString = documentSnapshot.getString("PsumRating");

                            // Update the document with the new average rating and increment OrderCount
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("ServiceDate", date);
                            updateData.put("ServiceTime", time);

                            firestore.collection("Service").document(ProviderId)
                                    .update(updateData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                                            // You can perform actions after updating the rating here
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error updating document", e);
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error getting document", e);
                    }
                });





                // time edit message share to the user Notification File
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DATE", date);
                editor.putString("TIME", time);
                editor.putString("USERID", USERID);
                editor.apply();

                Toast.makeText(TimeEditPage.this, "Message Sent!!!", Toast.LENGTH_SHORT).show();
                timeeditmsg = "(*)"+currentProName + " accept your request at \t" + time + "\t on \t" + date +" Are you free at that time? ";

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
}