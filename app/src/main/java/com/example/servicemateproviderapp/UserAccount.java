package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserAccount extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    private TextView FName, Address, City, Phone;
    private Button LogOut, myord;
    private TextView providerNameTextView, providerOrderTextView, providerRatingTextView, providerIdTextView, providerPhoneTextView;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        bottomNavigationView = findViewById(R.id.bottomnavigation);

        FName = findViewById(R.id.username);
        Address = findViewById(R.id.useraddress);
        City = findViewById(R.id.usercity);
        Phone = findViewById(R.id.userphone);
        LogOut = findViewById(R.id.logoutbtn);
        myord = findViewById(R.id.btnmyorders);
        providerNameTextView = findViewById(R.id.providerNameTextView);
        providerOrderTextView = findViewById(R.id.providerOrderTextView);
        providerRatingTextView = findViewById(R.id.providerRatingTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        bottomNavigationView.setOnItemSelectedListener(this);

        // Retrieve user details
        String UserId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference userDocRef = firestore.collection("users").document(UserId);
        userDocRef.addSnapshotListener(this, (value, error) -> {
            if (value != null && value.exists()) {
                FName.setText(value.getString("FName"));
                City.setText(value.getString("UCity"));
                Address.setText(value.getString("UAddress"));
                Phone.setText(value.getString("UPhone"));
            }
        });

        // Log Out
        LogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), User_Login.class));
            finish();
        });


        myord.setOnClickListener(v -> {
            // Retrieve provider details from SharedPreferences
            SharedPreferences preferences = getSharedPreferences("ProviderDetails", Context.MODE_PRIVATE);
            String providerName = preferences.getString("providerName", "");
            String providerOrder = preferences.getString("providerOrder", "");
            String providerRating = preferences.getString("providerRating", "");

            // Display provider details
            providerNameTextView.setText("Name:-"+providerName);
            providerOrderTextView.setText("Order:-"+providerOrder);
            providerRatingTextView.setText("Rating:-"+providerRating);




        });
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
}






//On Navigation Pending