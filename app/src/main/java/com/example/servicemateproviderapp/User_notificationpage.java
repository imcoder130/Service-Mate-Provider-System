package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class User_notificationpage extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    TextView text1, text2;
    Button b1, b2;

    String OrderCount;
    String sumRate;
    String sum;
    private String proId;
    public static String confirmtimeedit = "";
    public static String denytimeedit = "";
    BottomNavigationView bnv;
    Button btrate;



    LinearLayout layout;


    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    String CurrentuserId;
    String CurrentUsername; // Variable to hold the name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notificationpage);
        text1 = findViewById(R.id.textviewempty);
        text2 = findViewById(R.id.textviewblank);
        b1 = findViewById(R.id.btnnn1);
        b2 = findViewById(R.id.btnnnn2);
        bnv = findViewById(R.id.bottomnavigation1);
        btrate=findViewById(R.id.btnrating);
        layout=findViewById(R.id.completeLayout);

        bnv.setOnItemSelectedListener(this);


        btrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog(User_notificationpage.this);
            }
        });


        SharedPreferences userPrefs = getSharedPreferences("shareUId", Context.MODE_PRIVATE);
        String uuuID = userPrefs.getString("USERID", "");



        SharedPreferences pending = getSharedPreferences("Pending", Context.MODE_PRIVATE);
        String Pending = pending.getString("Pending", "");


//        String Complete="Complete";
//        if(Complete.equals(Pending))
//        {
//            layout.setVisibility(View.VISIBLE);
//        }

        SharedPreferences pen = getSharedPreferences("Pend", Context.MODE_PRIVATE);
        String Pen = pen.getString("Pend", "");

        String Complete = "Complete";
        String Pend = "Complete";

        if (Complete.equals(Pending) || Pend.equals(Pen)) {
            layout.setVisibility(View.VISIBLE);
        }





        // get time and date from time edit Page
        SharedPreferences shared1 = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String date = shared1.getString("DATE", "");
        String time = shared1.getString("TIME", "");
        String uId = shared1.getString("USERID", "");

        Log.d("TAG",uId);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        CurrentuserId = firebaseAuth.getCurrentUser().getUid(); // Initialize userId

        DocumentReference documentReference = firestore.collection("users").document(CurrentuserId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    CurrentUsername = value.getString("FName"); // Set the value of name here
                    // You can update UI elements here if needed
                }
            }
        });


        if (uuuID.equals(CurrentuserId)) {


            text2.setText(TimeEditPage.timeeditmsg); // Set the text here

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMyDialog();

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showMyDialogFordeny();


                }
            });

            SharedPreferences proPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String proChoice = proPrefs.getString("PRO_CHOICE", "");
            proId=proPrefs.getString("PID","");
            if ("CONFIRMED".equals(proChoice)) {
                layout.setVisibility(View.INVISIBLE);
                text1.setText(ProviderNotification.confirm);
                b1.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);
            } else if ("DENIED".equals(proChoice)) {
                layout.setVisibility(View.INVISIBLE);
                text1.setText(ProviderNotification.deny);
                b1.setVisibility(View.GONE);
                b2.setVisibility(View.GONE);
            } else if ("TimeEdit".equals(proChoice)) {
                layout.setVisibility(View.INVISIBLE);

                b1.setEnabled(true);
                b2.setEnabled(true);
            }
            layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.account) {
            Intent i = new Intent(getApplicationContext(), UserAccount.class);
            startActivity(i);
        } else if (id == R.id.Notification) {
            Intent i = new Intent(getApplicationContext(), User_notificationpage.class);
            startActivity(i);
        } else if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), User_home.class);
            startActivity(i);
        } else if (id == R.id.aboutus) {
            Intent i = new Intent(getApplicationContext(), Aboutus_page.class);
            startActivity(i);
        }
        return false;
    }



    // For get Rating
    private void showRatingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.rating, null);
        final RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        // Get the document reference

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setTitle("Rate Us");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference documentReference = firestore.collection("Providers").document(proId);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve OrderCount and sumRate from Firestore
                            int orderCountString = Integer.parseInt(documentSnapshot.getString("POrder"));
                            int sumRateString = Integer.parseInt(documentSnapshot.getString("PsumRating"));
                            // String  previousRating = documentSnapshot.getString("Prating");
                            float newRating = ratingBar.getRating();
                            int orderCount = orderCountString + 1;
                            int NewsumR = sumRateString + Math.round(newRating);
                            float avgR = (float) NewsumR / orderCount;

                            // Update the document with the new average rating and increment OrderCount
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("Prating", String.valueOf(avgR)); // Assuming Prating is stored as a string
                            updateData.put("POrder", String.valueOf(orderCount));
                            updateData.put("PsumRating", String.valueOf(NewsumR));
                            firestore.collection("Providers").document(proId)
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
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMyDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the Provider");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CurrentUsername != null) {
                    Toast.makeText(User_notificationpage.this, "Confirmation message sent to provider!!", Toast.LENGTH_SHORT).show();
                    confirmtimeedit =  "(*)"+CurrentUsername + " Confirmed Your request.";
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                    editor.putString("USER_CHOICE", "CONFIRMED");
                    editor.apply();
                } else {
                    Toast.makeText(User_notificationpage.this, "Name is not available", Toast.LENGTH_SHORT).show();
                }



            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when Cancel button is clicked
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }



    //for deny request

    private void showMyDialogFordeny() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Confirm Send");
        builder.setMessage("Are you sure you want to send this message to the Provider");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (CurrentUsername != null) {
                    Toast.makeText(User_notificationpage.this, "Denied message sent to provider!!", Toast.LENGTH_SHORT).show();
                    denytimeedit = "(*)" + CurrentUsername + " Denied Your request.";
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                    editor.putString("USER_CHOICE", "DENIED");
                    editor.apply();
                } else {
                    Toast.makeText(User_notificationpage.this, "Name is not available", Toast.LENGTH_SHORT).show();
                }


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when Cancel button is clicked
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }



}