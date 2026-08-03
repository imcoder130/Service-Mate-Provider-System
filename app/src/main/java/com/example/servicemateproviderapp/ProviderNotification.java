package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;


public class ProviderNotification extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    TextView txt1, txt2, txt3, txt4,PendingText,Name;
    Button btn1, btn2, btn3,PendingButton;

    LinearLayout layout,ButtonLayout;
    public static String confirm = "";
    public static String deny = "";
    BottomNavigationView bnv;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    String ProviderId;

    String currentProName;

    String Username;
    ArrayList<String> documentIds;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_notification);
        Name=findViewById(R.id.txtNAME);
        txt1 = findViewById(R.id.txtADD);
        txt2 = findViewById(R.id.txtDATE);
        txt3 = findViewById(R.id.txtTime);
        txt4 = findViewById(R.id.textviewconfirm);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        bnv = findViewById(R.id.bottomnavigation);

        PendingButton=findViewById(R.id.pendingId);
        PendingText=findViewById(R.id.pendingtextView);
        layout=findViewById(R.id.pendingLayout);
        ButtonLayout=findViewById(R.id.btnLayout);

        bnv.setOnItemSelectedListener(this);

        //for get Provider Name
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ProviderId = firebaseAuth.getCurrentUser().getUid(); // Initialize userId

        layout.setVisibility(View.INVISIBLE);


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


        // Second side for array of ids
        SharedPreferences sharedPreferencess = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE);
        String documentIdsString = sharedPreferencess.getString("document_ids", "");

        // Convert the string back to ArrayList


        documentIds = new ArrayList<>();
        if (!documentIdsString.isEmpty()) {
            String[] idsArray = documentIdsString.split(",");
            documentIds.addAll(Arrays.asList(idsArray));
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String address = sharedPreferences.getString("value1", "");
        String date = sharedPreferences.getString("value2", "");
        String time = sharedPreferences.getString("value3", "");
        String ssid = sharedPreferences.getString("ppId", "");
        String uuid = sharedPreferences.getString("UserId", "");
         Username=sharedPreferences.getString("UserName","");

        Log.d("TAG", "User Id" + uuid);


        // share User Id to Time edit Page
        SharedPreferences.Editor editor = getSharedPreferences("shareUId", Context.MODE_PRIVATE).edit();
        editor.putString("USERID", uuid);
        editor.apply();


        //  String ProId=sharedPreferences.getString("ProviderId","");

        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.INVISIBLE);
        btn3.setVisibility(View.INVISIBLE);

//        for (String documentId : documentIds) {
        if (ssid.equals(ProviderId)) {
            // If the element matches the target string

            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.VISIBLE);


            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();

            Log.d("TAG","username"+Username);
            //Toast.makeText(this, "UserName"+Username, Toast.LENGTH_SHORT).show();
            Name.setText("(*) Name : "+Username);
            txt1.setText("Address : "+address);
            txt2.setText("Date : "+date);
            txt3.setText("Time : "+time);


            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                    editor.putString("PRO_CHOICE", "TimeEdit");
                    editor.apply();
                    Intent i = new Intent(ProviderNotification.this, TimeEditPage.class);
                    startActivity(i);

                }
            });

            // Retrieve the user's choice from SharedPreferences
            SharedPreferences userPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String userChoice = userPrefs.getString("USER_CHOICE", "");

            // Set text based on the user's choice
            if ("CONFIRMED".equals(userChoice)) {
                txt4.setText(User_notificationpage.confirmtimeedit);

                layout.setVisibility(View.VISIBLE);
                PendingText.setText(Username);

                // Move the logic for PendingButton visibility here
                PendingButton.setVisibility(View.VISIBLE);
                ButtonLayout.setVisibility(View.VISIBLE);

               PendingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ButtonLayout.setVisibility(View.INVISIBLE);
                        btn3.setVisibility(View.INVISIBLE);

                        SharedPreferences sharedPreferences = getSharedPreferences("Pending", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Pending", "Complete");
                        editor.apply();

                        PendingButton.setText("Completed!!!");
                    }
                });



            } else if ("DENIED".equals(userChoice)) {
                txt4.setText(User_notificationpage.denytimeedit);
            }

            // Fetch the current user name from the database
            //String currentProName = getCurrentProName();

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showMyDialogforConform();

                }
            });

            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMyDialog();

                }
            });
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.account) {
            Intent i = new Intent(getApplicationContext(),ProviderAccount.class);
            startActivity(i);
            // Do nothing, already in the account page
        } else if (id == R.id.Notification) {
            Intent i = new Intent(getApplicationContext(), ProviderNotification.class);
            startActivity(i);
        } else if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), ProviderHome.class);
            startActivity(i);
        }
        else if (id == R.id.onoff) {
            Intent i = new Intent(getApplicationContext(), OnOffPage.class);
            startActivity(i);
        }
        else if (id == R.id.Reminder) {
            Intent i = new Intent(getApplicationContext(),provider_reminderpage.class);
            startActivity(i);
        }
        return false;
    }


    private void showMyDialogforConform() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the user");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Toast.makeText(ProviderNotification.this, "Confirmation message send to User!!", Toast.LENGTH_SHORT).show();


                confirm = "(*)" + currentProName + " Confirmed Your request.";

                // Save the user's choice in SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                editor.putString("PRO_CHOICE", "CONFIRMED");
                editor.putString("PID",ProviderId);
                editor.apply();

                // Set visibility of pending button and layout to VISIBLE
                layout.setVisibility(View.VISIBLE);
                PendingText.setText(Username);
                PendingButton.setVisibility(View.VISIBLE);
                ButtonLayout.setVisibility(View.VISIBLE);

                PendingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ButtonLayout.setVisibility(View.INVISIBLE);
                        btn3.setVisibility(View.INVISIBLE);

                        SharedPreferences sharedPreferences = getSharedPreferences("Pending", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Pending","Complete");
                        editor.apply();

                        PendingButton.setText("Completed!!!");
                    }
                });
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

    private void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the user");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ProviderNotification.this, "Denied message sent to user!!", Toast.LENGTH_SHORT).show();


                deny = "(*)" + currentProName + " Denied Your request.";



                // Save the user's choice in SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                editor.putString("PRO_CHOICE", "DENIED");
                editor.putString("PID",ProviderId);
                editor.apply();
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
