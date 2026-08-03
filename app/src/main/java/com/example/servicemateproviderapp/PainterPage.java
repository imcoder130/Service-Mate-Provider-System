package com.example.servicemateproviderapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PainterPage extends AppCompatActivity implements NavigationBarView.OnItemReselectedListener {
    ListView list1;
    Button btn1;
    BottomNavigationView bnv;
    String[] listDemo={"Wall repair and paint","1BHK Repair","2BHK fresh paint","2BHK Repaint","2BHK fresh paint","All Home paint","Others"};



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painter_page);
        list1=findViewById(R.id.listvieww);
        btn1=findViewById(R.id.nextButton);


        bnv=findViewById(R.id.bottomnavigation);
        bnv.setOnItemReselectedListener(this);



        //this code for small square box

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, listDemo);
        list1.setAdapter(arrayAdapter);
        list1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        list1.setOnItemClickListener((adapterView, view, i, l) -> {
            String items = (String) adapterView.getItemAtPosition(i);
            Toast.makeText(getApplicationContext(), "select by list of item" + items,
                    Toast.LENGTH_SHORT).show();
        });






        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spe="painter";
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