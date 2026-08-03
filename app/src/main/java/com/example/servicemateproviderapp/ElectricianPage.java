package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class ElectricianPage extends AppCompatActivity implements NavigationBarView.OnItemReselectedListener {
    ListView list;
    Button btn;
    BottomNavigationView bnv;
    String[] listDemo={"Fan Repair and Installation","Motor Repair","Solar panel installation Repair","Electricity breakdown","Switch board and socket repair and installation","Door bell installation and repair","Others"};


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electrition_page);


        bnv=findViewById(R.id.bottomnavigation);
        bnv.setOnItemReselectedListener(this);

        list = findViewById(R.id.listvieww);
        btn = findViewById(R.id.nextButton);

//this code for small square box

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, listDemo);
        list.setAdapter(arrayAdapter);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            String items = (String) adapterView.getItemAtPosition(i);
            Toast.makeText(getApplicationContext(), "select by list of item" + items,
                    Toast.LENGTH_SHORT).show();
        });

        List<String> selectedItems = new ArrayList<>();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spe="Electrician";
                Intent intent = new Intent(getApplicationContext(), ProviderList.class);
                intent.putExtra("spe",spe);
                startActivity(intent);

            }
        });


    }


    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.account) {
            Intent i = new Intent(getApplicationContext(), UserAccount.class);
            startActivity(i);
        } else if (id == R.id.Notification) {
            Intent i = new Intent(getApplicationContext(),User_notificationpage.class);
            startActivity(i);

        } else if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), User_home.class);
            startActivity(i);

        } else if (id == R.id.aboutus) {
            Intent i = new Intent(getApplicationContext(), Aboutus_page.class);
            startActivity(i);

        }
    }
}
