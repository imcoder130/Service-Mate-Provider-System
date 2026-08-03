package com.example.servicemateproviderapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Users_Rating extends AppCompatActivity {
    Button addButton;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    private String CurrentProviderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_rating);
        addButton = findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        CurrentProviderId = firebaseAuth.getCurrentUser().getUid(); // You forgot to initialize UserId

        DocumentReference documentReference = firestore.collection("Providers").document(CurrentProviderId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String currentProName = getString(Integer.parseInt("FName"));

                }
            }
        });


        // String currentProName = getCurrentProName();

        // Get current user name
//        String currentUser = getCurrentProName();
//
//        // Check if the recycle view and current user name are the same
//        // Assuming you have a variable named recycleViewUserName to represent the user name from the recycle view
//        String RealProvider = " ";// Replace this with the actual user name from the recycle view
//
//
//        // Check if the current user is a real provider
//        if (RealProvider(currentUser)) {
//            // The current user is a real provider, disable the addButton
//            addButton.setEnabled(false);
//        } else {
//            // The current user is not a real provider, enable the addButton
//            addButton.setEnabled(true);
//        }

    }



}

