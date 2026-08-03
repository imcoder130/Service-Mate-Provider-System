package com.example.servicemateproviderapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class User_home extends AppCompatActivity implements NavigationBarView.OnItemReselectedListener {

    ImageSwitcher imageSwitcher;
    ImageButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8;
    int images[] = {
            R.drawable.image2,
            R.drawable.acservice12,
            R.drawable.carpenter12,
            R.drawable.electricion12,
            R.drawable.painter12,
            R.drawable.pvcfloor
    };
    int count = images.length;
    int currentIndex = -1;
    BottomNavigationView bnv;
    Handler handler;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        imageSwitcher = findViewById(R.id.ImageSwitcher1);

        btn1 = findViewById(R.id.image1);
        btn2 = findViewById(R.id.image2);
        btn3 = findViewById(R.id.image3);
        btn4 = findViewById(R.id.image4);
        btn5 = findViewById(R.id.image5);
        btn6 = findViewById(R.id.image6);
        btn7 = findViewById(R.id.image7);
        btn8 = findViewById(R.id.image8);

        bnv=findViewById(R.id.bottomnavigation);
        bnv.setOnItemReselectedListener(this);




        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                return imageView;
            }
        });

        handler = new Handler();
        startAutoSwitching();


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), ElectricianPage.class);

                startActivity(i);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), PlumberPage.class);

                startActivity(i);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), CarpenterPage.class);

                startActivity(i);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), PestControlPage.class);

                startActivity(i);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), PainterPage.class);

                startActivity(i);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), AcServicesPage.class);

                startActivity(i);
            }
        });

        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),GardnerPage.class);

                startActivity(i);
            }
        });

        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), PvcFloorPage.class);

                startActivity(i);
            }
        });


    }





//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.bottomnavv, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.Notification) {
//           Intent i=new Intent(User_home2.this,UserAccount.class);
//           startActivity(i);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void startAutoSwitching() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentIndex = (currentIndex + 1) % count;
                imageSwitcher.setImageResource(images[currentIndex]);
                startAutoSwitching(); // Recursive call for continuous switching
            }
        }, 2000); // Set the duration for each image (in milliseconds), e.g., 2000 for 2 seconds
    }


    //bottom Navigation code
    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {


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
    }


}
