
package com.example.mobile_application.ui.passenger_ride_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.PassengerRideHistoryDTO;

import java.util.ArrayList;
import java.util.List;

public class PassengerRideHistoryAdapter
        extends RecyclerView.Adapter<PassengerRideHistoryAdapter.ViewHolder> {

    private List<PassengerRideHistoryDTO> rides = new ArrayList<>();
    private OnRideClickListener listener;

    public interface OnRideClickListener {
        void onViewDetails(PassengerRideHistoryDTO ride);
    }

    public PassengerRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<PassengerRideHistoryDTO> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PassengerRideHistoryDTO ride = rides.get(pos);

        String route = shortenText(ride.getStartLocation(), 30)
                + " → " + shortenText(ride.getEndLocation(), 30);
        h.tvRoute.setText(route);
        h.tvDate.setText(ride.getDate() != null ? ride.getDate() : "");
        h.tvTime.setText(String.format("%s - %s",
                ride.getStartedAt() != null ? ride.getStartedAt() : "?",
                ride.getEndedAt() != null ? ride.getEndedAt() : "?"));
        h.tvPrice.setText(String.format("%.0f RSD", ride.getPrice()));
        h.tvDriver.setText(ride.getDriverName());

        if (ride.isRated()) {
            h.tvRating.setText(String.format("★ %.1f", ride.getRideDriverRating()));
            h.tvRating.setVisibility(View.VISIBLE);
        } else {
            h.tvRating.setText("Not rated");
            h.tvRating.setVisibility(View.VISIBLE);
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
        TextView tvRoute, tvDate, tvTime, tvPrice, tvDriver, tvRating;
        Button btnDetails;

        ViewHolder(View v) {
            super(v);
            tvRoute = v.findViewById(R.id.tvRoute);
            tvDate = v.findViewById(R.id.tvDate);
            tvTime = v.findViewById(R.id.tvTime);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDriver = v.findViewById(R.id.tvDriver);
            tvRating = v.findViewById(R.id.tvRating);
            btnDetails = v.findViewById(R.id.btnDetails);
        }
    }
}
