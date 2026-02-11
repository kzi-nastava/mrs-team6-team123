package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.DriverAssignedRideDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignedRideAdapter extends RecyclerView.Adapter<AssignedRideAdapter.ViewHolder> {

    private List<DriverAssignedRideDTO> rides = new ArrayList<>();
    private final OnRideActionListener listener;

    public AssignedRideAdapter(OnRideActionListener listener) {
        this.listener = listener;
    }

    public void setRides(List<DriverAssignedRideDTO> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assigned_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriverAssignedRideDTO ride = rides.get(position);

        // Set locations
        holder.tvStartLocation.setText(ride.getStartLocation());
        holder.tvEndLocation.setText(ride.getEndLocation());

        // Set passengers
        if (ride.getPassengerNames() != null && !ride.getPassengerNames().isEmpty()) {
            holder.tvPassengers.setText(String.join(", ", ride.getPassengerNames()));
        } else {
            holder.tvPassengers.setText("No passengers");
        }

        // Set price
        holder.tvPrice.setText(String.format("$%.2f", ride.getEstimatedPrice()));

        // Set scheduled time if available
        if (ride.getScheduledAt() != null && !ride.getScheduledAt().isEmpty()) {
            holder.tvScheduled.setVisibility(View.VISIBLE);
            holder.tvScheduled.setText(formatScheduledTime(ride.getScheduledAt()));
        } else {
            holder.tvScheduled.setVisibility(View.GONE);
        }

        // Button listeners
        holder.btnStart.setOnClickListener(v -> listener.onStartRide(ride.getRideId()));

        // Only allow starting the first (soonest) ride
        if (position == 0 && ("CREATED".equals(ride.getStatus()) || "ACCEPTED".equals(ride.getStatus()))) {
            holder.btnStart.setVisibility(View.VISIBLE);
            holder.btnStart.setEnabled(true);
            holder.btnStart.setAlpha(1.0f);
        } else {
            holder.btnStart.setVisibility(View.VISIBLE);
            holder.btnStart.setEnabled(false);
            holder.btnStart.setAlpha(0.5f);
        }
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    private String formatScheduledTime(String scheduledAt) {
        try {
            // Parse the incoming format (assuming ISO 8601 or similar)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(scheduledAt);

            // Format to readable format: "10:00 AM Jan 01"
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a MMM dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            // If parsing fails, return original string
            return scheduledAt;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartLocation, tvEndLocation, tvPassengers, tvPrice, tvScheduled;
        Button btnStart;

        ViewHolder(View itemView) {
            super(itemView);
            tvStartLocation = itemView.findViewById(R.id.tv_start_location);
            tvEndLocation = itemView.findViewById(R.id.tv_end_location);
            tvPassengers = itemView.findViewById(R.id.tv_passengers);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvScheduled = itemView.findViewById(R.id.tv_scheduled);
            btnStart = itemView.findViewById(R.id.btn_start_ride);
        }
    }

    public interface OnRideActionListener {
        void onStartRide(Long rideId);
    }
}
