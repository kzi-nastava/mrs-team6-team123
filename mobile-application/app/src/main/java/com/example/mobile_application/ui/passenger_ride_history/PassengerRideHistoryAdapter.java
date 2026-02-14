
package com.example.mobile_application.ui.passenger_ride_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.PassengerRideHistoryDTO;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.FavoritesService;
import com.example.mobile_application.service.TokenManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryAdapter
        extends RecyclerView.Adapter<PassengerRideHistoryAdapter.ViewHolder> {

    private List<PassengerRideHistoryDTO> rides = new ArrayList<>();
    private OnRideClickListener listener;
    private Set<Long> favoriteRouteIds = new HashSet<>();
    private java.util.Map<Long, Long> favoriteIdMap = new java.util.HashMap<>();
    private FavoritesService favoritesService;
    private TokenManager tokenManager;
    private Long currentUserId;

    public interface OnRideClickListener {
        void onViewDetails(PassengerRideHistoryDTO ride);
    }

    public PassengerRideHistoryAdapter(OnRideClickListener listener) {
        this.listener = listener;
        this.favoritesService = ApiClient.getInstance().create(FavoritesService.class);
        this.tokenManager = ApiClient.getTokenManager();
        this.currentUserId = tokenManager.getUserId();
    }

    public void setRides(List<PassengerRideHistoryDTO> rides) {
        this.rides = rides;
        if (currentUserId != null) {
            loadFavoriteRoutes();
        } else {
            notifyDataSetChanged();
        }
    }

    private void loadFavoriteRoutes() {
        if (currentUserId == null)
            return;

        favoritesService.getFavoriteRoutes(currentUserId)
                .enqueue(new Callback<List<com.example.mobile_application.dto.FavoriteRouteDTO>>() {
                    @Override
                    public void onResponse(Call<List<com.example.mobile_application.dto.FavoriteRouteDTO>> call,
                            Response<List<com.example.mobile_application.dto.FavoriteRouteDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            favoriteRouteIds.clear();
                            favoriteIdMap.clear();
                            for (com.example.mobile_application.dto.FavoriteRouteDTO fav : response.body()) {
                                favoriteRouteIds.add(fav.getRouteId());
                                favoriteIdMap.put(fav.getRouteId(), fav.getId());
                            }
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<com.example.mobile_application.dto.FavoriteRouteDTO>> call,
                            Throwable t) {
                    }
                });
    }

    private boolean isFavorited(Long routeId) {
        return favoriteRouteIds.contains(routeId);
    }

    private void toggleFavorite(PassengerRideHistoryDTO ride, Toast context) {
        if (currentUserId == null)
            return;

        if (isFavorited(ride.getRouteId())) {
            removeFavorite(ride);
        } else {
            addFavorite(ride);
        }
    }

    private void addFavorite(PassengerRideHistoryDTO ride) {
        favoritesService.addFavoriteRoute(currentUserId, ride.getRouteId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            loadFavoriteRoutes();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
    }

    private void removeFavorite(PassengerRideHistoryDTO ride) {
        Long favoriteId = favoriteIdMap.get(ride.getRouteId());
        if (favoriteId != null) {
            favoritesService.removeFavoriteRoute(currentUserId, favoriteId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                loadFavoriteRoutes();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                        }
                    });
        }
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

        boolean isFav = isFavorited(ride.getRouteId());
        h.btnFavorite.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        h.btnFavorite.setOnClickListener(v -> {
            toggleFavorite(ride, null);
        });

        h.btnDetails.setOnClickListener(v -> listener.onViewDetails(ride));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    private String shortenText(String text, int max) {
        if (text == null)
            return "";
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvDate, tvTime, tvPrice, tvDriver, tvRating;
        Button btnDetails;
        ImageButton btnFavorite;

        ViewHolder(View v) {
            super(v);
            tvRoute = v.findViewById(R.id.tvRoute);
            tvDate = v.findViewById(R.id.tvDate);
            tvTime = v.findViewById(R.id.tvTime);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDriver = v.findViewById(R.id.tvDriver);
            tvRating = v.findViewById(R.id.tvRating);
            btnDetails = v.findViewById(R.id.btnDetails);
            btnFavorite = v.findViewById(R.id.btnFavorite);
        }
    }
}
