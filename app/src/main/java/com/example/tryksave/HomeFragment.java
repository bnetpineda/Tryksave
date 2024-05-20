package com.example.tryksave;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TravelInfoAdapter adapter;
    private List<TravelInfo> travelInfoList;

    private double totalDistance = 0.0;
    private double totalDuration = 0.0;
    private double totalEstimatedFarePrice = 0.0;

    private TextView totalDistanceTextView;
    private TextView totalDurationTextView;
    private TextView totalEstimatedFarePriceTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_home, container, false);
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView userHomeFullName = view1.findViewById(R.id.TextViewHomeName);
        if (user == null) {
            startActivity(new Intent(getContext(), SignIn.class));
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        travelInfoList = new ArrayList<>();
        adapter = new TravelInfoAdapter(getContext(), travelInfoList);
        recyclerView.setAdapter(adapter);

        fetchTravelInfoData();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).collection("userDetails")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Access each document's data
                                    String name = document.getString("name");
                                    // Use the data as needed
                                    userHomeFullName.setText("Hi, " + name);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }return view1;
    }

    private void fetchTravelInfoData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Replace with dynamic user ID if needed

        db.collection("users").document(user.getUid())
                .collection("travelInfo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TravelInfo travelInfo = documentSnapshot.toObject(TravelInfo.class);
                            travelInfoList.add(travelInfo);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "No documents found in travelInfo collection!");
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting documents", e));
    }
}