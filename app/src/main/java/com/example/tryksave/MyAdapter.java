package com.example.tryksave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<User> list;

    public MyAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        User user  = list.get(position);
        holder.distance.setText(user.getDistance());
        holder.duration.setText(user.getDuration());
        holder.estimatedCost.setText(user.getEstimatedCost());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView distance, duration, estimatedCost;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            distance = itemView.findViewById(R.id.tvdistance);
            duration = itemView.findViewById(R.id.tvduration);
            estimatedCost = itemView.findViewById(R.id.tvestimatedCost);
        }
    }
}
