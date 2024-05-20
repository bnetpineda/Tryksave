package com.example.tryksave;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecordsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TravelInfoAdapter adapter;
    private List<TravelInfo> travelInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        travelInfoList = new ArrayList<>();
        adapter = new TravelInfoAdapter(getContext(), travelInfoList);
        recyclerView.setAdapter(adapter);

        fetchTravelInfoData();
        return view;
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