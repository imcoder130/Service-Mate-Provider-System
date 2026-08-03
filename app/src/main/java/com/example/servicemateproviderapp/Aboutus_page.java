package com.example.servicemateproviderapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Aboutus_page extends AppCompatActivity implements NavigationBarView.OnItemReselectedListener {
    BottomNavigationView bnv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus_page);

        bnv=findViewById(R.id.bottomnavigation);

        bnv.setOnItemReselectedListener(this);

    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.account) {
            Intent i = new Intent(getApplicationContext(), UserAccount.class);
            startActivity(i);
        } else if (id==R.id.Notification) {
            Intent i = new Intent(getApplicationContext(), User_notificationpage.class);
            startActivity(i);

        } else if (id==R.id.home) {
            Intent i = new Intent(getApplicationContext(), User_home.class);
            startActivity(i);

        } else if (id==R.id.aboutus) {
            Intent i = new Intent(getApplicationContext(), Aboutus_page.class);
            startActivity(i);

        }
    }
}