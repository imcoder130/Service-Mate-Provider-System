package com.example.servicemateproviderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProviderList extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore firestore;

    FirebaseAuth firebaseAuth;

    ArrayList<String> name;
    ArrayList<String> phone;
    ArrayList<Float> rating;
    ArrayList<String> ProId;
    ArrayList<String> TotalOrder;

    MyAdapter adapter;
    String ProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        recyclerView = findViewById(R.id.rvc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        name = new ArrayList<>();
        phone = new ArrayList<>();
        rating = new ArrayList<>();
        ProId = new ArrayList<>();
        TotalOrder =new ArrayList<>();

        adapter = new MyAdapter(this, name, rating, phone,TotalOrder, ProId, firestore);


//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        MyAdapter adapter = new MyAdapter(context, nameList, ratingList, orderList, providerList, firestore);
// Updated adapter initialization
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void loadData() {

        //Array of ids
        ArrayList<String> documentIds = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference providersRef = db.collection("Providers");
        Bundle extras = getIntent().getExtras();
        String specialization = extras.getString("spe");

        providersRef.whereEqualTo("PSpecialization", specialization)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        name.clear();
                        rating.clear();
                        phone.clear();
                        ProId.clear();
                        TotalOrder.clear();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String providerName = documentSnapshot.getString("FName");
                            String providerPhone = documentSnapshot.getString("PPhone");

                            // Get Prating as an Object
                            float ratingD = Float.parseFloat(documentSnapshot.getString("Prating"));

                            // Parse Prating as a float

//                            if (pratingObject instanceof String) {
//                                // Parse as float if it's a string
//                                try {
//                                    ratingValue = Float.parseFloat((String) pratingObject);
//                                } catch (NumberFormatException e) {
//                                    // Handle parsing error, maybe set a default value
//                                }
//                            } else if (pratingObject instanceof Double) {
//                                // Parse as float if it's a double
//                                ratingValue = ((Double) pratingObject).floatValue();
//                            }

                            String PId = documentSnapshot.getId();
                            name.add(providerName);
                            rating.add(ratingD); // Add the rating value
                            ProId.add(PId);
                            // Statically setting rating to 3
                            TotalOrder.add("4"); // Statically setting order to 4
                            phone.add(providerPhone);
                            documentIds.add(PId);// ids stored in array
                        }



                        //for send the ids array using shared preference
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String documentId : documentIds) {
                            stringBuilder.append(documentId);
                            stringBuilder.append(",");
                        }
                        String documentIdsString = stringBuilder.toString();

                        // Store the string in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("document_ids", documentIdsString);
                        editor.apply();

                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error getting documents: " + e.getMessage());
                    }
                });
    }
}