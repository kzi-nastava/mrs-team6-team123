package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.model.DriverRideHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class DriverRideHistoryAdapter extends
        RecyclerView.Adapter<DriverRideHistoryAdapter.ViewHolder> {

    private List<DriverRideHistoryDTO> rides = new ArrayList<>();
    private final OnRideClickListener listener;

    public DriverRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<DriverRideHistoryDTO> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverRideHistoryDTO ride = rides.get(position);
        String routeStr = ride.getStartLocation() + " -> " + ride.getEndLocation();
        holder.tvFromTo.setText(routeStr);
        holder.tvDate.setText(ride.getDate().toString());
        holder.itemView.setOnClickListener(v ->
                listener.onRideClick(ride)
        );
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromTo, tvDate;
        ViewHolder(View itemView) {
            super(itemView);
            tvFromTo = itemView.findViewById(R.id.tvFromTo);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    public interface OnRideClickListener {
        void onRideClick(DriverRideHistoryDTO ride);
    }
}
