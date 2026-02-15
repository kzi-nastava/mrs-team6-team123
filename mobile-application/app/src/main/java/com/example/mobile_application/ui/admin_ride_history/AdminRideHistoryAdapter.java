
package com.example.mobile_application.ui.admin_ride_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.AdminRideHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class AdminRideHistoryAdapter
        extends RecyclerView.Adapter<AdminRideHistoryAdapter.ViewHolder> {

    private List<AdminRideHistoryDTO> rides = new ArrayList<>();
    private OnRideClickListener listener;

    public interface OnRideClickListener {
        void onViewDetails(AdminRideHistoryDTO ride);
    }

    public AdminRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<AdminRideHistoryDTO> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        AdminRideHistoryDTO ride = rides.get(pos);

        String route = shortenText(ride.getStartLocation(), 25)
                + " → " + shortenText(ride.getEndLocation(), 25);
        h.tvRoute.setText(route);
        h.tvDate.setText(ride.getDate() != null ? ride.getDate() : "");
        h.tvPrice.setText(String.format("%.0f RSD", ride.getPrice()));
        h.tvDistance.setText(String.format("%.1f km", ride.getTotalDistance()));
        h.tvDriver.setText(ride.getDriverName());

        // Cancelled badge
        if (ride.isCancelled()) {
            h.tvCancelled.setVisibility(View.VISIBLE);
            h.tvCancelled.setText("Cancelled");
        } else {
            h.tvCancelled.setVisibility(View.GONE);
        }

        // Panic badge
        if (ride.isPanicTriggered()) {
            h.tvPanic.setVisibility(View.VISIBLE);
            h.tvPanic.setText("🚨 PANIC");
        } else {
            h.tvPanic.setVisibility(View.GONE);
        }

        h.btnDetails.setOnClickListener(v -> listener.onViewDetails(ride));
    }

    @Override
    public int getItemCount() { return rides.size(); }

    private String shortenText(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDate, tvPrice, tvDistance,
                tvDriver, tvCancelled, tvPanic;
        Button btnDetails;

        ViewHolder(View v) {
            super(v);
            tvRoute = v.findViewById(R.id.tvRoute);
            tvDate = v.findViewById(R.id.tvDate);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDistance = v.findViewById(R.id.tvDistance);
            tvDriver = v.findViewById(R.id.tvDriver);
            tvCancelled = v.findViewById(R.id.tvCancelled);
            tvPanic = v.findViewById(R.id.tvPanic);
            btnDetails = v.findViewById(R.id.btnDetails);
        }
    }
}