package com.example.servicemateproviderapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Provider_Login extends AppCompatActivity {

    EditText Email,Password;
    Button loginButton;
    TextView  regText;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_login);

        Email=findViewById(R.id.proemail);
        Password=findViewById(R.id.propass);
        loginButton=findViewById(R.id.btnLogin);
        regText=findViewById(R.id.textreg);
        firebaseAuth = FirebaseAuth.getInstance();



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Provideremail = Email.getText().toString().trim(); // Corrected variable name
                String Providerpassword = Password.getText().toString().trim(); // Corrected variable name

                if (TextUtils.isEmpty(Provideremail)) {
                    Email.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(Providerpassword)) {
                   Password.setError("Password is Required");
                    return;
                }
                if (Providerpassword.length() < 6) {
                    Password.setError("Password must be at least 6 characters");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(Provideremail, Providerpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Provider_Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),ProviderHome.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Provider_Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        regText.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Provider_registration.class);
                startActivity(i);
            }
        });

    }
}