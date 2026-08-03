package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProviderHome extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    private int position=0;
    private Button playButton;
    private VideoView videoView;
    BottomNavigationView bnv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        videoView = findViewById(R.id.video);
        playButton = findViewById(R.id.playbutton);
        bnv=findViewById(R.id.bottomnavigation);

        bnv.setOnItemSelectedListener(this);

        MediaController mediaController = new MediaController(ProviderHome.this);
        mediaController.setAnchorView(videoView);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mediapath = "android.resource://" + getPackageName() + "/" + R.raw.video;
                videoView.setMediaController(mediaController);
                Uri uri = Uri.parse(mediapath);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Account) {
            Intent i = new Intent(getApplicationContext(), ProviderAccount.class);
            startActivity(i);
        } else if (id == R.id.Notification) {
            Intent i = new Intent(getApplicationContext(), ProviderNotification.class);
            startActivity(i);

        } else if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), ProviderHome.class);
            startActivity(i);

        } else if (id == R.id.Reminder) {
            Intent i = new Intent(getApplicationContext(), provider_reminderpage.class);
            startActivity(i);

        } else if (id == R.id.onoff) {
            Intent i = new Intent(getApplicationContext(),OnOffPage.class);
            startActivity(i);
        }

        return false;
    }
}