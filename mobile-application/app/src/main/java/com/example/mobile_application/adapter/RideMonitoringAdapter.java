package com.example.mobile_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.RideMonitoringDTO;

import java.util.ArrayList;
import java.util.List;

public class RideMonitoringAdapter extends
        RecyclerView.Adapter<RideMonitoringAdapter.ViewHolder> {
    private final List<RideMonitoringDTO> originalList;
    private final List<RideMonitoringDTO> filteredList;
    private final OnRideClickListener listener;

    public RideMonitoringAdapter(List<RideMonitoringDTO> rides,
                                 OnRideClickListener listener) {
        this.originalList = rides;
        this.filteredList = new ArrayList<>(rides);
        this.listener = listener;
    }
    public interface OnRideClickListener {
        void onRideClick(Long rideId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_monitoring, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideMonitoringDTO ride = filteredList.get(position);

        String routeStr = ride.getFrom() + " -> " + ride.getTo();
        holder.tvRoute.setText(routeStr);
        holder.tvDriver.setText(ride.getDriverName());
        holder.tvLicencePlate.setText(ride.getLicencePlate());

        holder.itemView.setOnClickListener(v ->
                listener.onRideClick(ride.getRideId())
        );
    }

    public void filter(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerQuery = query.toLowerCase();

            for (RideMonitoringDTO ride : originalList) {
                if (ride.getDriverName() != null &&
                        ride.getDriverName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(ride);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setData(List<RideMonitoringDTO> newRides) {
        originalList.clear();
        originalList.addAll(newRides);

        filteredList.clear();
        filteredList.addAll(newRides);

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDriver, tvLicencePlate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvLicencePlate = itemView.findViewById(R.id.tvLicencePlate);
        }
    }
}
