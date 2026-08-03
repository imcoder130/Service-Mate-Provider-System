package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.net.eap.EapSessionConfig;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Provider_registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

  private   EditText PName,PEmail,PPassword,PPhoneNo;
    private Button registerBtn;
    private TextView logintext;

    Spinner spinner11,spinner22;
    String spce[]={"Electrician", "plumber","carpenter","pest control","painter","AC Service","Gardner","PVC Floor"};
    String specc[]={"Shivajinagar","Deccan Gymkhana","JM Road","FC Road","Pune Camp","Sadashiv Peth","Narayan Peth","Budhwar Peth","Kothrud","Bavdhan","Warje","Karve Nagar", "Hingne Khurd", "Sinhagad Road"};

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    String ProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_registration);
        PName=findViewById(R.id.editname);
        PEmail=findViewById(R.id.editEmail);
        PPassword=findViewById(R.id.editpass);
        PPhoneNo=findViewById(R.id.editphone);

        spinner11=findViewById(R.id.spinner);
        spinner22=findViewById(R.id.spinner0);
        registerBtn=findViewById(R.id.proregbtn);
        logintext=findViewById(R.id.logintext);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        // To check if user is already logged in
//        if (firebaseAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), Provider_Login.class));
//            finish();
//        }



        //code for Spinner for get specialization of provider
        spinner11.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,spce);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner11.setAdapter(arrayAdapter);

        //code for Spinner for get specialization of provider
        spinner22.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter1=new ArrayAdapter(this, android.R.layout.simple_spinner_item,specc);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner22.setAdapter(arrayAdapter1);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ProviderEmail = PEmail.getText().toString().trim();
                String ProviderPassword = PPassword.getText().toString().trim();
                String ProviderName=PName.getText().toString();
                String ProviderPhone=PPhoneNo.getText().toString();
                String Providerspecialization=spinner11.getSelectedItem().toString();
                String ProviderAddress=spinner22.getSelectedItem().toString();


                if (TextUtils.isEmpty(ProviderEmail)) {
                    PEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(ProviderPassword)) {
                    PPassword.setError("Password is Required");
                    return;
                }
                if (PPassword.length() < 6) {
                    PPassword.setError("Password must be at least 6 characters");
                    return;
                }

                if(TextUtils.isEmpty(ProviderPhone) || ProviderPhone.length() != 10 || !TextUtils.isDigitsOnly(ProviderPhone))
                {
                    PPhoneNo.setError("Please Enter Valid 10-digit Mobile Number");
                    return;
                }
                if (TextUtils.isEmpty(ProviderName)) {
                    PName.setError("Name is Required");
                    return;

                }



                firebaseAuth.createUserWithEmailAndPassword(ProviderEmail, ProviderPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Provider_registration.this, "User created", Toast.LENGTH_SHORT).show();

                                   ProviderId=firebaseAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference=firestore.collection("Providers").document(ProviderId);
                                    Map<String,Object> user=new HashMap<>();
                                    user.put("FName",ProviderName);
                                    user.put("PEmail",ProviderEmail);
                                    user.put("PPhone",ProviderPhone);
                                    user.put("PSpecialization",Providerspecialization);
                                    user.put("PAddress", ProviderAddress);
                                    user.put("Prating","3");
                                    user.put("POrder","1");
                                    user.put("PsumRating","2");
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG","On Success: User profile is created "+ProviderId);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG","On Failure: "+e.toString());
                                        }
                                    });


                                    Intent intent = new Intent(getApplicationContext(), Provider_Login.class);
                                    startActivity(intent);
                                    finish(); // Finish current activity after starting new one
                                } else {
                                    Toast.makeText(Provider_registration.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Provider_Login.class);
                startActivity(i);
            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}