package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class User_registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText Uname, Uphone, Uemail, Upassword;
    private Button regButton;
    private TextView logintxt;
    Spinner spin;
    String spce1[]={"Shivajinagar","Deccan Gymkhana","JM Road","FC Road","Pune Camp","Sadashiv Peth","Narayan Peth","Budhwar Peth","Kothrud","Bavdhan","Warje","Karve Nagar", "Hingne Khurd", "Sinhagad Road"};
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        Uname = findViewById(R.id.editname);
        Uphone = findViewById(R.id.editphone);
        Uemail = findViewById(R.id.editEmail);
        Upassword = findViewById(R.id.editpassword);
        regButton = findViewById(R.id.btnReg);
        spin=findViewById(R.id.spinner1);
        logintxt = findViewById(R.id.textLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();

        // To check if user is already logged in
//        if (firebaseAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), User_Login.class));
//            finish();
//        }


        //code for Spinner for get address for user
        spin.setOnItemSelectedListener( this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item,spce1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrayAdapter);


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = Uemail.getText().toString().trim();
                String Password = Upassword.getText().toString().trim();
                String name=Uname.getText().toString();
                String phone=Uphone.getText().toString();
                String Address=spin.getSelectedItem().toString();

                if (TextUtils.isEmpty(Email)) {
                    Uemail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    Upassword.setError("Password is Required");
                    return;
                }
                if (Password.length() < 6) {
                    Upassword.setError("Password must be at least 6 characters");
                    return;
                }
                if(TextUtils.isEmpty(phone) || phone.length() != 10 || !TextUtils.isDigitsOnly(phone))
                {
                    Uphone.setError("Please Enter Valid 10-digit Mobile Number");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Uname.setError("Name is Required");
                    return;

                }




                //User Registration
                firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(User_registration.this, "User created", Toast.LENGTH_SHORT).show();

                                    UserId=firebaseAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference=firestore.collection("users").document(UserId);
                                    Map<String,Object>user=new HashMap<>();
                                    user.put("FName",name);
                                    user.put("UEmail",Email);
                                    user.put("UPhone",phone);
                                    user.put("UAddress",Address);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG","On Success: User profile is created "+UserId);


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("TAG","On Failure: "+e.toString());
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(), User_Login.class);
                                    startActivity(intent);
                                    finish(); // Finish current activity after starting new one
                                } else {
                                    Toast.makeText(User_registration.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),User_Login.class);
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
