package com.example.servicemateproviderapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final Context context;
    private final ArrayList<String> name_id;
    private final ArrayList<Float> rating_id;
    private final ArrayList<String> order_id;
    private final ArrayList<String> providerId;
    private final ArrayList<String> providerPhone;
    private final FirebaseFirestore firestore;
    static ArrayList<ServiceStop> serviceStops; // Make it static to access it from RequestForm activity

    public MyAdapter(Context context, ArrayList<String> name_id, ArrayList<Float> rating_id, ArrayList<String> providerPhone, ArrayList<String> order_id, ArrayList<String> providerId, FirebaseFirestore firestore) {
        this.context = context;
        this.name_id = name_id;
        this.rating_id = rating_id;
        this.order_id = order_id;
        this.providerId = providerId;
        this.providerPhone = providerPhone;
        this.firestore = firestore;
        this.serviceStops = new ArrayList<>(); // Initialize service stop data list
        fetchServiceStopData(); // Fetch service stop data when adapter is initialized
    }

    private void fetchServiceStopData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference providersRef = db.collection("ServiceStop");

        providersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("TAG", "Service stop data fetched successfully.");
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String startDate = documentSnapshot.getString("StartDate");
                        String endDate = documentSnapshot.getString("EndDate");
                        String providerId = documentSnapshot.getString("ProviderId");

                        serviceStops.add(new ServiceStop(providerId, startDate, endDate));
                    }
                    notifyDataSetChanged(); // Notify adapter after fetching data
                })
                .addOnFailureListener(e -> Log.e("TAG", "Error fetching service stop data: " + e.getMessage()));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_providerentry, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameTextView.setText(name_id.get(position));
        holder.orderTextView.setText(order_id.get(position));
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(rating_id.get(position))));
        holder.ProviderId.setText(providerId.get(position));
        holder.PhoneNo.setText(providerPhone.get(position));

        String proId = holder.ProviderId.getText().toString();

        Log.d("TAG", "ProviderId: " + proId);

        // Check if the provider ID is in the list of service stops
        boolean isServiceStopped = isServiceStopped(proId);
        // Set the add button enabled since we want to disable only dates in the DatePickerDialog
        // holder.btnAdd.setEnabled(!isServiceStopped);

        holder.btnAdd.setOnClickListener(v -> {
            String Pid = holder.ProviderId.getText().toString();
            Intent intent = new Intent(holder.itemView.getContext(), RequestForm.class);
            intent.putExtra("PPId", Pid);
            holder.itemView.getContext().startActivity(intent);


            String providerName = name_id.get(position);
            String providerOrder = order_id.get(position);
            String providerRating = String.valueOf(rating_id.get(position));

            // Store provider details in SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("ProviderDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("providerName", providerName);
            editor.putString("providerOrder", providerOrder);
            editor.putString("providerRating", providerRating);
            editor.apply();


        });
    }

    private boolean isServiceStopped(String providerId) {
        for (ServiceStop serviceStop : serviceStops) {
            if (serviceStop.getProviderId().equals(providerId)) {
                // Since we only want to disable dates in the DatePickerDialog, we don't need to check service stop here
                // Implement the check in RequestForm activity instead
                return false; // Return false by default
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return name_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView orderTextView;
        TextView PhoneNo;
        RatingBar ratingBar;
        Button btnAdd;
        TextView ProviderId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textname);
            orderTextView = itemView.findViewById(R.id.totalOrder);
            ratingBar = itemView.findViewById(R.id.ratingbar1);
            btnAdd = itemView.findViewById(R.id.ADDbtn);
            ProviderId = itemView.findViewById(R.id.hideId);
            PhoneNo = itemView.findViewById(R.id.textorder);
        }
    }

    // Change the access level of ServiceStop to at least package-private
    static class ServiceStop {
        private final String providerId;
        private final String startDate;
        private final String endDate;

        public ServiceStop(String providerId, String startDate, String endDate) {
            this.providerId = providerId;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getProviderId() {
            return providerId;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }
    }
}













