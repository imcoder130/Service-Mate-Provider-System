package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ProviderAccount extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    private TextView FName, Address, Phone;
    private Button LogOut;

    String ProviderId;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_account);
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        FName = findViewById(R.id.providername);
        Address = findViewById(R.id.provideraddress);
        Phone = findViewById(R.id.providerphone);
        LogOut = findViewById(R.id.logoutbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ProviderId = firebaseAuth.getCurrentUser().getUid(); // You forgot to initialize UserId

        DocumentReference documentReference = firestore.collection("Providers").document(ProviderId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    FName.setText(value.getString("FName"));
                    Address.setText(value.getString("PAddress"));
                    Phone.setText(value.getString("PPhone"));
                }
            }
        });

        // Log Out
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Provider_Login.class));
                finish();
            }
        });
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

}
