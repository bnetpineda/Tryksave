package com.example.tryksave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tryksave.TravelInfo;

import java.util.List;

public class TravelInfoAdapter extends RecyclerView.Adapter<TravelInfoAdapter.TravelInfoViewHolder> {
    private List<TravelInfo> travelInfoList;
    private Context context;

    public TravelInfoAdapter(Context context, List<TravelInfo> travelInfoList) {
        this.context = context;
        this.travelInfoList = travelInfoList;
    }

    @NonNull
    @Override
    public TravelInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_travel_info, parent, false);
        return new TravelInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelInfoViewHolder holder, int position) {
        TravelInfo travelInfo = travelInfoList.get(position);
        if (travelInfo != null) {
            holder.distanceText.setText(travelInfo.getDistanceText());
            holder.durationText.setText(travelInfo.getDurationText());
            holder.estimatedFarePrice.setText(travelInfo.getEstimatedFarePrice());
        }
    }

    @Override
    public int getItemCount() {
        return travelInfoList.size();
    }

    public static class TravelInfoViewHolder extends RecyclerView.ViewHolder {
        TextView distanceText;
        TextView durationText;
        TextView estimatedFarePrice;

        public TravelInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            distanceText = itemView.findViewById(R.id.distance_text);
            durationText = itemView.findViewById(R.id.duration_text);
            estimatedFarePrice = itemView.findViewById(R.id.estimated_fare_price);
        }
    }
}
